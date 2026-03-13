# Simulatest

If you work on a project with a database, you know the pain: tests that corrupt each other's data, `@Before`/`@After` cleanup boilerplate everywhere, brittle `DELETE FROM` or `TRUNCATE` statements between tests, and suites that slow to a crawl because they recreate the schema for every test class.

Simulatest eliminates all of that. It is a JVM toolkit made of two independent, composable tools:

1. **Insistence Layer**: a transactional sandbox for your database
2. **Environments**: composable, OOP test fixtures organized as a tree

---

## The Insistence Layer

*Insist, insist, insist... but never persist.*

What if your database had an undo button? You set up data, run your code, and then undo everything. The database returns to exactly the state it was in before, as if nothing happened.

That's what the Insistence Layer does. It wraps your database connection and gives you a **checkpoint stack**: push a checkpoint before you make changes, pop it when you're done, and everything rolls back. The data is real while it exists: queryable, joinable, subject to constraints. But it never persists beyond the checkpoint.

Checkpoints nest arbitrarily deep. Each pop rolls back exactly one level.

```
+1  →  push checkpoint
       do work: INSERTs, UPDATEs, DELETEs. All real, all queryable.
-1  →  pop checkpoint. Everything undone, no trace.
```

Under the hood, this is built on JDBC savepoints, a standard database feature. Any database that supports savepoints works with the Insistence Layer.

Three main use cases:

| Use Case | How |
|----------|-----|
| **Test isolation** | The framework pushes/pops checkpoints around environments and tests automatically |
| **Dev seeding** | Push a checkpoint, run environments to populate an empty local DB, explore the app, pop when done |
| **Production safety net** | Push a checkpoint, run a risky migration, inspect results, pop to undo if wrong |

The Insistence Layer is not just a test tool. It works anywhere.

---

## Environments

In database-heavy projects, test setup code tends to be duplicated across test classes. Or worse, every test builds the world from scratch. Environments solve this by letting you define setup logic once and compose it into a hierarchy.

An Environment is a Java class that sets up data. It declares a parent via `@EnvironmentParent`, the same way a class extends a superclass: it trusts that the parent's data already exists.

```
CompanyEnvironment               → creates a company
├── EmployeeEnvironment          → creates employees (trusts company exists)
│   ├── PayrollEnvironment       → creates payroll records (trusts employees exist)
│   └── PermissionsEnvironment   → creates access roles (trusts employees exist)
└── ProductEnvironment           → creates products (trusts company exists)
```

Each environment runs once. Tests declare which environment they need via `@UseEnvironment`. Simulatest builds the tree, resolves the ordering, and executes everything.

When combined with the Insistence Layer, the magic happens: after each environment runs, a checkpoint is pushed. After its subtree completes, the checkpoint is popped. This means sibling environments never see each other's data. `PayrollEnvironment` and `PermissionsEnvironment` each start from the same `EmployeeEnvironment` state, completely unaffected by each other. Tests at the same level are isolated too. And the entire suite rolls back at the end.

No `@After`, no `DELETE FROM`, no `TRUNCATE`, no `@DirtiesContext`. No cleanup code at all.

---

## JUnit Integration

Works with both JUnit 4 (custom runner) and JUnit 5 (custom TestEngine). DI plugins for Spring, Guice, and Jakarta CDI are auto-discovered via ServiceLoader.

```java
@UseEnvironment(PayrollEnvironment.class)
public class PayrollTest {

    @Test
    void shouldCalculateEmployeeSalary() {
        // Company, Employee, and Payroll data already exist.
        // The entire ancestor chain ran automatically.
        // After this test, everything rolls back.
    }
}
```

---

## Two Tools, One Toolkit

The Insistence Layer and Environments are independent. Use the Insistence Layer alone for checkpoint-based isolation in any context. Use Environments alone for composable test fixtures. Together, they eliminate the two biggest time sinks in database-heavy projects: setting up test data and cleaning it up.

Environments define *what* data to create. The Insistence Layer ensures *none of it persists*.

---

*Inspired by a production-ready Smalltalk implementation from Objective Solutions.*

## License

Licensed under the [Apache License 2.0](LICENSE).
