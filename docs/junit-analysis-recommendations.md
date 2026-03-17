# JUnit Implementation Analysis & Recommendations

## Scope reviewed
- JUnit 4 integration (`simulatest-environment` module).
- JUnit 5 custom engine + extensions (`simulatest-environment-junit5` module).
- JUnit usage patterns in test suites across modules.
- Maven dependency and Surefire configuration related to JUnit.

## High-priority recommendations

1. **Make JUnit 4 runner reset logic exception-safe**
   - `SimulatestJUnit4ClassRunner#runChild` resets Insistence Layer *after* `super.runChild(...)`, but not inside `finally`.
   - Recommendation: wrap `super.runChild(...)` in `try/finally` so state resets even if execution aborts unexpectedly.

2. **Fix filtering semantics in `AbstractEnvironmentJUnitRunner#filter`**
   - Current code forwards `Filter` to each child runner but never removes filtered-out runners nor throws `NoTestsRemainException` when all tests are excluded.
   - Recommendation: track survivors, remove empty runners, and throw `NoTestsRemainException` if none remain to match JUnit `Filterable` contract.

3. **Avoid `UniqueId` collisions for environment nodes**
   - JUnit 5 engine uses `def.getName()` (simple class name) for `UniqueId` segment.
   - Different environments with identical simple names from different packages could collide.
   - Recommendation: use fully qualified class name (`def.getEnvironmentClass().getName()`) in `UniqueId` segments.

4. **Add explicit concurrency policy for JUnit 5 engine internals**
   - `JupiterDelegatingClassDescriptor` reuses a static singleton `Launcher`.
   - Recommendation: document and test thread-safety under parallel execution, or create launcher per descriptor execution if isolation is preferred.

## Medium-priority recommendations

5. **Improve scan performance in `UseEnvironmentClassScanner`**
   - Package/classpath scanning currently accepts all class names (`name -> true`) and filters only after loading classes.
   - Recommendation: add a class-name predicate (e.g., likely test naming patterns / package constraints) to reduce scanning overhead in large projects.

6. **Stabilize dynamic test IDs independent of display names**
   - Dynamic IDs are generated with `captured.displayName + "#" + index`.
   - Recommendation: incorporate source info (method name, class, invocation index) so IDs remain stable and less sensitive to display-name changes.

7. **Strengthen coverage for discovery/filter edge cases**
   - Add tests for:
     - duplicate simple environment names,
     - fully filtered test plans,
     - nested-class selector behavior,
     - parallel engine execution.

## Low-priority recommendations

8. **Reduce mixed JUnit 4 + JUnit 5 style where not required**
   - Project intentionally supports both, but many modules still use JUnit 4 assertions and lifecycle APIs.
   - Recommendation: gradually migrate non-runner tests to Jupiter (`org.junit.jupiter.api.Assertions`, `@BeforeEach`, etc.) to simplify maintenance.

9. **Prefer `assertThrows` over manual `try/catch` in tests**
   - Several tests still manually catch exceptions.
   - Recommendation: use `assertThrows` for clearer intent and better failure diagnostics.

10. **Standardize Surefire plugin version across modules**
    - Root and submodules use different Surefire versions.
    - Recommendation: centralize on one version in parent `pluginManagement` to reduce behavioral drift between modules.

## Suggested rollout order
1. Runner/filter correctness fixes (items 1 and 2).
2. JUnit 5 identity/isolation hardening (items 3 and 4).
3. Discovery and test coverage hardening (items 5-7).
4. Gradual style/tooling cleanup (items 8-10).
