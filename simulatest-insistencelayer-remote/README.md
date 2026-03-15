# Insistence Layer Remote

Your app runs in one process. Your tests run in another.

Maybe you're running Selenium tests against a Spring Boot app inside Docker. Maybe your REST API integration tests spin up the service in a separate JVM and talk to it over HTTP. In all these cases, the test driver and the application are **not in the same process**.

The Insistence Layer works by wrapping the database connection and managing a savepoint stack. But savepoints are connection-scoped: they only exist on the connection that created them. If your tests run in a different process, they can't reach the app's connection. They can't push savepoints. They can't roll back. The undo button is out of reach.

This module fixes that. It puts a thin TCP wire between the two processes, so your tests can control the savepoint stack remotely, as if they were in the same JVM.

## Quick Start

Add the dependency to **both** the application and the test project:

```xml
<dependency>
    <groupId>org.simulatest</groupId>
    <artifactId>simulatest-insistencelayer-remote</artifactId>
    <version>0.0.2</version>
</dependency>
```

**App process** (embed the server):

```java
InsistenceLayerServer server = new InsistenceLayerServer(manager, 4242);
server.start();
```

**Test process** (use the remote manager):

```java
InsistenceLayerManager manager = new RemoteInsistenceLayerManager("localhost", 4242);

manager.increaseLevel();     // sent over TCP, app creates savepoint
manager.resetCurrentLevel(); // sent over TCP, app rolls back to savepoint
manager.decreaseLevel();     // sent over TCP, app removes savepoint
```

That's it. `RemoteInsistenceLayerManager` extends `InsistenceLayerManager`, so it works everywhere the local manager works: pass it to `EnvironmentDatabaseRunner`, use it in JUnit rules, or call it directly.

## How It Works

```
Test Process                              App Process
┌────────────────────────────┐           ┌────────────────────────────┐
│ RemoteInsistenceLayerManager│───TCP───→│ InsistenceLayerServer       │
│ (drop-in replacement)      │  3 cmds  │ (delegates to real manager) │
└────────────────────────────┘           └────────────────────────────┘
                                                    │
                                                    ▼
                                              ┌──────────┐
                                              │ Database │
                                              └──────────┘
```

The protocol is three single-byte commands: increase, decrease, reset. One byte in, one byte out. No serialization libraries, no code generation, no external dependencies. The default port is 4242, but you can pass any port number to the constructor (or 0 to let the OS pick one).

## Setup Examples

### Spring Boot

```java
@Configuration
@Profile("test")
public class InsistenceLayerConfig {

    @Bean(destroyMethod = "stop")
    public InsistenceLayerServer insistenceLayerServer(DataSource dataSource) throws Exception {
        ConnectionWrapper wrapper = new ConnectionWrapper(dataSource.getConnection());
        InsistenceLayerManager manager = InsistenceLayerManagerFactory.build(wrapper);

        InsistenceLayerServer server = new InsistenceLayerServer(manager, 4242);
        server.start();
        return server;
    }
}
```

### Docker / CI

```yaml
# docker-compose.test.yml
services:
  app:
    build: .
    environment:
      - SPRING_PROFILES_ACTIVE=test
    ports:
      - "8080:8080"
      - "4242:4242"

  tests:
    build:
      context: .
      dockerfile: Dockerfile.tests
    environment:
      - INSISTENCE_HOST=app
      - INSISTENCE_PORT=4242
    depends_on:
      - app
```

```java
String host = System.getenv().getOrDefault("INSISTENCE_HOST", "localhost");
int port = Integer.parseInt(System.getenv().getOrDefault("INSISTENCE_PORT", "4242"));

InsistenceLayerManager manager = new RemoteInsistenceLayerManager(host, port);
```

## Use Cases

**Selenium / Playwright UI tests.** Your browser tests drive the app through the UI while the Insistence Layer rolls back every test's database changes. No cleanup scripts, no test database rebuilds.

**REST API integration tests.** Spin up the service, hit it with HTTP requests, and the savepoint stack ensures each test starts clean.

**Microservice testing.** Test against a real running service instead of mocks. The Insistence Layer keeps the database pristine between tests.

## API

| Class | Role |
|-------|------|
| `InsistenceLayerServer` | Embed in the app. Binds a TCP port, delegates to a real `InsistenceLayerManager`. |
| `RemoteInsistenceLayerManager` | Use in tests. Drop-in replacement for `InsistenceLayerManager` that sends commands over TCP. |
| `InsistenceLayerClient` | Low-level TCP client. Used internally by `RemoteInsistenceLayerManager`. |
| `InsistenceLayerProtocol` | Protocol constants. Three commands, two response codes. |

## Error Handling and Debugging

All errors throw `InsistenceLayerException` with a descriptive message. No silent failures, no retries. Tests fail fast.

Both sides emit structured log messages at every step, so you can correlate what happened across processes:

```
# Server side: command failed (full stack trace preserved)
ERROR [InsistenceLayer Remote] Command 0x02 failed at level 0
  java.lang.IllegalStateException: Cannot decrease level: already at level 0

# Client side: server unreachable
ERROR [InsistenceLayer Remote] Failed to connect to db-host:4242
  java.net.ConnectException: Connection refused

# Client side: connection lost mid-test
ERROR [InsistenceLayer Remote] Lost connection to db-host:4242 during command 0x01
  java.io.EOFException
```

Every error log includes the host, port, command byte, and current level so you can pinpoint exactly what went wrong without guesswork.

| Scenario | What happens |
|----------|-------------|
| Server unreachable | `InsistenceLayerException` on the first command, with host and port in the message. |
| Server-side failure (e.g. decrease at level 0) | Server logs the full stack trace, sends the error message over the wire, client throws `InsistenceLayerException`. |
| Connection drops mid-test | `InsistenceLayerException` with the command that was in-flight and the server address. |

## Limitations

- **Single client.** The server accepts one connection at a time. If a second client connects, it waits until the first disconnects. This is by design: savepoints live on one JDBC connection and must not be manipulated concurrently.
- **No automatic reconnection.** If the connection drops, the next command throws. Restart the server and recreate the `RemoteInsistenceLayerManager` to recover.
- **Local level counter.** The remote manager tracks the savepoint level locally to avoid round-trips. Under normal operation this stays in sync. If a network error causes a command to be executed on the server but the response is lost, the counter may drift. This is unlikely in practice (localhost or Docker network), but worth knowing.
