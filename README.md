# Simulatest

If you work on a project with a database, you know the pain: tests that corrupt each other's data, `@Before`/`@After` cleanup boilerplate everywhere, brittle `DELETE FROM` or `TRUNCATE` statements between tests, and integration suites that slow to a crawl because they recreate the schema for every test class.

Simulatest eliminates all of that. It is a JVM toolkit made of two independent, composable tools:

1. **Insistence Layer**: a transactional sandbox for your database
2. **Environments**: composable, OOP test fixtures organized as a tree

---

## The Insistence Layer

*Insist, insist, insist... but never persist.*

What if your database had an undo button? You set up data, run your code, and then undo everything. The database returns to exactly the state it was in before, as if nothing happened.

That's what the Insistence Layer does. It wraps your database connection and manages a **level stack**. Increase the level before you make changes, decrease it when you're done, and everything is undone. The data is real while it exists: queryable, joinable, subject to constraints. But it never persists beyond its level.

Levels nest arbitrarily deep. Each `decreaseLevel()` undoes exactly one level.

```
increaseLevel()
    do work: INSERTs, UPDATEs, DELETEs. All real, all queryable.
decreaseLevel()
    everything undone, no trace.
```

Under the hood, the level stack is built on JDBC savepoints, a standard database feature. Any database that supports savepoints works with the Insistence Layer.

Three main use cases:

| Use Case | How |
|----------|-----|
| **Test isolation** | The framework increases/decreases levels around environments and tests automatically |
| **Dev seeding** | Increase a level, run environments to populate an empty local DB, explore the app, decrease when done |
| **Production safety net** | Increase a level, run a risky migration, inspect results, decrease to undo if wrong |

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

When combined with the Insistence Layer, the magic happens: after each environment runs, the level is increased. After its subtree completes, the level is decreased and everything that subtree did is undone. This means sibling environments never see each other's data. `PayrollEnvironment` and `PermissionsEnvironment` each start from the same `EmployeeEnvironment` state, completely unaffected by each other. Tests at the same level are isolated too, via `resetCurrentLevel()`. And the entire suite is undone at the end.

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
        // After this test, the level resets. Clean slate.
    }
}
```

---

## Performance: The Advantage Nobody Talks About

There's a side effect of this architecture that deserves its own section: **it's dramatically faster**.

Think about what a traditional integration test suite does. Say you have 500 tests and they all need the same 200 rows of reference data: countries, roles, categories, configurations. The standard approach:

```
For EACH of the 500 integration tests:
  TRUNCATE everything
  INSERT 200 rows of reference data   ← the same 200 rows, again
  INSERT test-specific data
  Run the test
```

That's **100,000 redundant INSERTs**. The same reference data, inserted and destroyed 500 times. Plus TRUNCATE calls, foreign key cascades, and index rebuilds. Every single integration test pays the full cost of building the world from scratch.

With the Insistence Layer, the math changes completely:

```
INSERT 200 rows of reference data       ← once, just once
  increaseLevel()
  ├─ INSERT test-specific data → run Test A → decreaseLevel()   ← instant undo
  increaseLevel()
  ├─ INSERT test-specific data → run Test B → decreaseLevel()   ← instant undo
  ...498 more tests, each with instant undo...
```

**200 INSERTs instead of 100,000.** The reference data is inserted once and stays there. Undoing a level is nearly free, the database just discards uncommitted changes. No row-by-row deletion, no index rebuilding, no cascade checks. It doesn't matter if the test inserted 5 rows or 5 million; the cost of decreasing a level is the same.

And this compounds through the environment tree:

```
ReferenceData (countries, roles)             ← inserted ONCE
  increaseLevel()  → level 1
  │
  ├─ Members (users, profiles)               ← inserted ONCE for all member tests
  │   increaseLevel()  → level 2
  │   ├─ Test: member login         → resetCurrentLevel() (instant, members still there)
  │   ├─ Test: profile update       → resetCurrentLevel() (instant, members still there)
  │   └─ Test: member deletion      → resetCurrentLevel() (instant, members still there)
  │   decreaseLevel()  → back to level 1 (members gone, ref data still there)
  │
  ├─ Catalog (books, categories)             ← inserted ONCE for all catalog tests
  │   increaseLevel()  → level 2
  │   ├─ Test: search by author     → resetCurrentLevel()
  │   ├─ Test: add duplicate book   → resetCurrentLevel()
  │   ...
```

Every level of the tree multiplies the savings. The deeper the tree, the bigger the gap between Simulatest and the traditional approach. You're not looking at a 2x improvement; you're looking at **orders of magnitude fewer database operations**.

| Approach | Setup Operations | Cleanup Operations |
|---|---|---|
| Traditional | N × R INSERTs | N TRUNCATEs |
| Simulatest | R INSERTs (once) | N level resets (near-zero cost) |

*(N = number of integration tests, R = shared reference rows)*

The best part? You don't optimize for this. You don't write caching logic or parallel setup code. You just organize your environments into a tree that reflects how your data depends on other data, the natural, readable structure, and the performance comes for free.

**The honest caveat:** designing good environments is not trivial. You need to think carefully about what data belongs at each level, what depends on what, and how to draw the boundaries. It takes a thoughtful process to get the tree right. But once you do, once it clicks... it's like seeing the Matrix. You'll look at integration test suites full of duplicated setup and teardown and wonder how you ever tolerated it. You won't go back.

---

## Two Tools, One Toolkit

The Insistence Layer and Environments are independent. Use the Insistence Layer alone for level-based isolation in any context. Use Environments alone for composable test fixtures. Together, they eliminate the two biggest time sinks in database-heavy projects: setting up test data and cleaning it up.

Environments define *what* data to create. The Insistence Layer ensures *none of it persists*.

---

*Inspired by a production-ready Smalltalk implementation from Objective Solutions.*

## License

Licensed under the [Apache License 2.0](LICENSE).
