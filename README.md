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

### Is DFS the optimal traversal for environment reuse?

For the current Simulatest model (tree-shaped dependencies, parent state reused by children, sibling branches isolated after completion), a depth-first traversal is the right default and is effectively optimal for setup reuse:

- It maximizes **prefix reuse**. Once a parent path is created, DFS executes all descendants before tearing that path down.
- It minimizes **environment switches** between unrelated branches (the expensive part in setup-heavy suites).
- It naturally matches level-based rollback semantics: descend (create more specific state), then unwind (discard branch-specific state), then move to the next sibling.

Could something beat DFS? Only in extended scenarios where plain trees are no longer the full cost model:

- If setup costs are highly asymmetric, a **cost-aware sibling ordering** (still DFS) can reduce wall clock time.
- If dependencies are a DAG (shared nodes with multiple parents), the scheduling problem changes and may need a different planner.
- If environments are pure/read-only and parallel-safe, selective **parallel execution of disjoint subtrees** can improve throughput.

So the short answer is: for tree dependencies with rollback isolation and a goal of maximal state reuse, DFS is the best baseline. Improvements usually come from smarter sibling ordering or safe parallelism, not replacing DFS itself.

### Orchestrated parallelism: how to do it safely

If you want parallel speedups **without letting same-level tests interfere with each other**, use coordinated, branch-aware scheduling:

1. **Keep DFS semantics per worker**
   - Each worker executes its assigned branch depth-first to preserve maximum reuse.

2. **Parallelize only independent branches**
   - Two branches can run together only when their nearest common ancestor is already materialized and neither branch mutates shared global state outside rollback control.

3. **Use one transactional context per worker**
   - Give each worker its own DB connection/session + insistence level stack.
   - Never share a mutable transaction context across workers.

4. **Enforce a same-level write policy**
   - Default policy: tests/environments at the same level run in isolation (separate worker contexts).
   - If an environment is marked non-parallel-safe, serialize it with a lock/tag (for example: `db-schema`, `external-api`, `filesystem`).

5. **Add capability metadata to environments**
   - `parallelSafe=true|false`
   - `resourceLocks={...}`
   - Optional estimated cost for smarter scheduling.

6. **Use a bounded scheduler**
   - Build ready queues from the tree.
   - Dispatch runnable branches up to `maxWorkers` while respecting lock conflicts.
   - Prefer heavier branches first to reduce tail latency.

7. **Fail-safe fallback**
   - If lock contention or unsafe markers dominate, automatically degrade that region of the tree to sequential DFS.

This gives you a hybrid model: **DFS for reuse, orchestration for concurrency**. In practice, that is usually the best throughput/isolation trade-off.

### Multiple connections vs orchestration: what is the best strategy?

Short answer: **use both, with orchestration as the control plane and multiple connections as the execution primitive**.

- **Multiple connections only** (fire everything in parallel) is risky:
  - good raw throughput potential
  - poor safety unless you add coordination for shared resources
  - easy to create lock contention and flaky cross-test interference

- **Orchestration only** (single connection, scheduled ordering) is safe:
  - maximal determinism
  - but limited parallel speedup

- **Best practical model**: orchestrated fork/join + isolated connections per parallel branch:
  - scheduler decides *where* parallelism is allowed
  - each parallel branch gets its own transaction context/connection
  - branches join at the parent before any shared-dependent step continues

A good mental model is:

```
run(node):
  if node is parallel-safe and children are independent:
    parallelize(leftSubtree on ConnA)
    parallelize(rightSubtree on ConnB)
    wait/join
  else:
    run children sequentially (DFS)

  if next step needs shared mutable state:
    execute sequentially or under a resource lock
```

When shared state is needed, prefer this order of options:

1. **Avoid sharing** (best): duplicate/partition resource per worker (schema, tenant, queue namespace).
2. **Serialize only the critical section**: keep parallelism outside the shared segment, lock only the shared operation.
3. **Degrade subtree to sequential DFS** if the shared dependency dominates and lock contention is high.

Rule of thumb:

- CPU/IO-heavy but isolated setup → increase `maxWorkers` and run independent branches in parallel.
- DB-hotspot or global singleton dependency → keep that zone sequential/locked.
- Start conservative, measure contention/latency, then widen parallel windows gradually.


### JUnit 5 configuration (natural defaults)

Simulatest now follows JUnit-style configuration with a safe default:

- Default: **sequential** (`junit.jupiter.execution.parallel.enabled=false`).
- If the user enables Jupiter parallelism, Simulatest forwards those settings to delegated Jupiter execution.
- When the Insistence Layer is active, Simulatest keeps execution sequential by default to preserve rollback isolation.
- Expert opt-in exists for forcing parallel with Insistence Layer:
  - `simulatest.execution.parallel.allow-insistence=true`

Typical configuration examples:

```properties
# Sequential by default (implicit)
junit.jupiter.execution.parallel.enabled=false
```

```properties
# Parallel Jupiter execution
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.config.strategy=fixed
junit.jupiter.execution.parallel.config.fixed.parallelism=4
```

```properties
# Expert mode: allow parallel even with Insistence Layer (use with care)
simulatest.execution.parallel.allow-insistence=true
```

**The honest caveat:** designing good environments is not trivial. You need to think carefully about what data belongs at each level, what depends on what, and how to draw the boundaries. It takes a thoughtful process to get the tree right. But once you do, once it clicks... it's like seeing the Matrix. You'll look at integration test suites full of duplicated setup and teardown and wonder how you ever tolerated it. You won't go back.

---

## Two Tools, One Toolkit

The Insistence Layer and Environments are independent. Use the Insistence Layer alone for level-based isolation in any context. Use Environments alone for composable test fixtures. Together, they eliminate the two biggest time sinks in database-heavy projects: setting up test data and cleaning it up.

Environments define *what* data to create. The Insistence Layer ensures *none of it persists*.

---

*Inspired by a production-ready Smalltalk implementation from Objective Solutions.*

## License

Licensed under the [Apache License 2.0](LICENSE).
