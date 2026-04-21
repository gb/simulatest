# Simulatest

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/java-17%2B-blue.svg)](#quick-start)
[![Simulatest](https://img.shields.io/maven-central/v/org.simulatest/simulatest-insistencelayer?label=simulatest)](https://central.sonatype.com/artifact/org.simulatest/simulatest-insistencelayer)

If you work on a database-backed system, you've felt this:

- Tests interfering with each other
- Setup and cleanup boilerplate everywhere (`@Before`, `@After`, `DELETE FROM`, `TRUNCATE`)
- Tests rebuilding the same data over and over

Simulatest removes all of that. It replaces the traditional setup/cleanup model with something simpler: reversible state and structured fixtures.

It is a JVM toolkit built on two ideas:

1. Reversible database state
2. A better way to write test fixtures

See it in action: [simulatest-examples](https://github.com/gb/simulatest-examples).

---

## The Insistence Layer

> What if your database had an *undo button*?

You set up data, run your test, and then undo everything, instantly. The database returns to exactly the previous state, as if nothing happened.

No cleanup. No side effects. No cross-test contamination.

The Insistence Layer turns your database into a transactional sandbox. You work normally:

- `INSERT`, `UPDATE`, `DELETE`
- Data is real, queryable, and constrained

But nothing persists beyond its scope.

```java
increaseLevel();

// do work: INSERTs, UPDATEs, DELETEs

decreaseLevel(); // everything undone
```

Internally, this is powered by JDBC savepoints, a standard database feature.

It has these properties:

- Levels can nest arbitrarily
- Each level is fully isolated
- Undo is instant, regardless of how much data was written

---

## Environments

A better way to write test fixtures:

Environments are composable system states, not setup scripts.

Most test suites treat fixtures as setup code: duplicated across tests, rebuilt per test, tightly coupled to execution order.

Simulatest treats fixtures as composable system states. This is not a convenience feature. It is a different way to model test data.

An Environment represents a cohesive state of the system. Instead of splitting by entity, you group by what the system needs together.

```
CompanyEnvironment               → creates a company
├── EmployeeEnvironment          → creates employees (trusts company exists)
│   ├── PayrollEnvironment       → creates payroll records (trusts employees exist)
│   └── PermissionsEnvironment   → creates access roles (trusts employees exist)
└── ProductEnvironment           → creates products (trusts company exists)
```

- Each level defines a complete, reusable baseline
- Child environments extend that baseline
- Tests pick the level of the world they need

> If your environments look like your database tables, you're probably doing it wrong.

`UserEnvironment` + `OrderEnvironment` + `ProductEnvironment` forces every test to reassemble the world. `CheckoutEnvironment` hands you a complete, working domain state.

Tests don't build data. They declare the world they want to run in:

```java
@UseEnvironment(PayrollEnvironment.class)
class PayrollTest {

    @Test
    void shouldCalculateSalary() {
        // Base + Payroll data already exist
    }
}
```

The framework resolves dependencies, builds the environment tree, and executes each environment exactly once.

---

## How It Works Together

Simulatest walks the environment tree depth-first. Tests sharing an environment run consecutively under that environment's level. After each environment runs, a new level is created. After its subtree completes, that level is undone.

Result:

- The environment is built once and reused across every test that needs it
- Sibling environments never see each other's data
- Tests at the same level start from an identical state

You stop thinking about cleaning up data. You stop thinking about rebuilding data. You just define the world once, and move through it.

---

## Quick Start

Add the JUnit 5 integration (Maven):

```xml
<dependency>
    <groupId>org.simulatest</groupId>
    <artifactId>simulatest-environment-junit-platform</artifactId>
    <version>0.1.0</version>
    <scope>test</scope>
</dependency>
```

Define an Environment. It inserts shared setup once; the framework replays it for every test that needs it:

```java
public class CompanyEnvironment implements Environment {

    @Override
    public void run() {
        // insert shared setup: companies, reference data, config rows.
        // use JdbcTemplate, a JPA repository, or raw JDBC.
        companyRepository.save(new Company("Acme"));
    }
}
```

Write a test that uses it:

```java
@UseEnvironment(CompanyEnvironment.class)
class CompanyTest {

    @Test
    void acmeIsAvailable() {
        // CompanyEnvironment has already run.
        assertTrue(companyRepository.existsByName("Acme"));
    }

    @Test
    void changesDoNotBleed() {
        companyRepository.save(new Company("Temp"));
        // after this test finishes, 'Temp' is gone.
        // 'Acme' stays, because the environment owns it.
    }
}
```

Nest environments with `@EnvironmentParent` to build richer state on top of a base:

```java
@EnvironmentParent(CompanyEnvironment.class)
public class EmployeeEnvironment implements Environment {

    @Override
    public void run() {
        // 'Acme' is already present here.
        Company acme = companyRepository.findByName("Acme");
        employeeRepository.save(new Employee("Ana", acme));
    }
}
```

Environments and tests are regular classes; inject collaborators via your DI framework of choice (Spring, Guice, and Jakarta CDI plugins are auto-discovered). For full setup with Spring Boot, JPA, and H2, see the [examples repo](https://github.com/gb/simulatest-examples).

---

## What You Don't Need Anymore

- `@Before` / `@After` setup and cleanup
- `DELETE FROM` or `TRUNCATE` scripts
- Recreating schemas between tests
- Rebuilding the same reference data repeatedly
- Fighting test ordering or hidden dependencies

---

## Performance

Shared environments are built once and reused across every test that needs them. Level resets between tests are instant: no deletes, no truncates, just discarded uncommitted changes. The result is orders of magnitude fewer database operations than a traditional integration suite. No configuration needed; the order comes from the tree.

---

## Why This Feels Different

Traditional integration testing treats the database as something fragile: you constantly rebuild it, clean it, and try to keep tests from interfering with each other.

Simulatest treats it as something reversible and structured.

That shift removes most of the incidental complexity around integration tests.

---

## Known Limitations

Savepoint-based isolation is powerful but has real tradeoffs. Know these before adopting:

- **Single connection.** Simulatest drives one underlying connection for the whole suite, even when callers request multiple connections. Code under test must go through the `InsistenceLayerDataSource` to participate in the sandbox. Code that opens a second DataSource or talks to the database outside the wrapper won't see environment data and its writes won't roll back.
- **No true `REQUIRES_NEW`.** Because every connection the pool hands out resolves to the same underlying session, Spring's `REQUIRES_NEW` transactions are not actually isolated from the outer transaction the way they would be in production. If your code relies on inner-transaction isolation (for example, writing an audit row that must survive a parent rollback), that semantic breaks here.
- **DDL is not rolled back.** `CREATE TABLE`, `ALTER`, and similar statements commit implicitly on most databases. Run schema migrations (Flyway, Liquibase, `ddl-auto`) before the suite starts, not inside an Environment.
- **Single-threaded within a JVM.** The connection model is not thread-safe. Don't enable JUnit 5 parallel execution inside a test JVM; levels will interleave and corrupt each other. You can still parallelize across JVMs with Maven Surefire's `forkCount` (each fork runs an independent Insistence Layer). If the forks share a real database, give each one its own schema or instance so they don't see each other's writes through the DB.
- **Database-specific rollback quirks.** Savepoints don't cover everything: PostgreSQL sequences keep advancing, MySQL leaves gaps in auto-increment, Oracle sequence caches don't rewind. Tests that assert on generated IDs can flake across runs.

If any of these are dealbreakers for your suite, Testcontainers with per-test TRUNCATE or Spring's `@Transactional` rollback may be a better fit.

---

## Integrations

- **JVM languages**: any JVM language (Java, Kotlin, and Scala are shown in the [examples repo](https://github.com/gb/simulatest-examples))
- **Test frameworks**: JUnit 4 (custom runner), JUnit 5 and JUnit 6 (custom TestEngine on the JUnit Platform)
- **Dependency injection**: Spring, Guice, and Jakarta CDI plugins, auto-discovered via ServiceLoader
- **Databases**: any JDBC driver with savepoint support (H2, PostgreSQL, MySQL, Oracle, SQL Server all qualify)

---

*Inspired by a Smalltalk implementation from Objective Solutions.*

## License

Licensed under the [Apache License 2.0](LICENSE).
