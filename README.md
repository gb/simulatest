# Simulatest

If you've ever worked on a project with heavy relational database usage, you know the pain. Every test needs data. Setting up that data takes longer than the test itself. And cleaning it up? Even worse. You write `DELETE FROM` scripts, `@Before` and `@After` blocks, truncate-and-reseed routines. All fragile, all slow, all repeated across every test class.

Simulatest makes all of that disappear.

---

## The Insistence Layer

*Insist, insist, insist — but never persist.*

Imagine you could tell your relational database: *"Remember this moment."*

Then you do whatever you want. Insert rows, update records, delete tables, run a migration. The data is real. You can query it, join against it, build reports from it. It's all there.

And then you say: *"Go back to that moment."*

Everything you did is undone. The relational database is exactly as it was. No cleanup. No scripts. No trace.

That's the Insistence Layer. A transactional sandbox for any relational database. It uses JDBC savepoints, a feature already built into every major relational database, to create checkpoints you can roll back to. And checkpoints are nestable. You can push one inside another, as deep as you need. Each rollback takes you back exactly one level.

We call it `+1` and `-1`. Push a checkpoint, do your work, pop the checkpoint. Gone.

The Insistence Layer is not a test tool. It's a standalone sandbox. You can use it for anything.

For **testing**, it gives you automatic isolation between test cases with zero cleanup code. For **local development**, when your relational database is empty and you need data to explore the app, you push a checkpoint, populate it, play around, and pop the checkpoint when you're done. For **production**, when you're about to run a risky data migration, you push a checkpoint first, inspect the results, and if something looks wrong, pop it. The relational database is untouched.

Yes, production. If your relational database supports savepoints, the Insistence Layer works there. It's not magic. It's just a well-managed transaction that never commits.

---

## Environments

Now for the second problem: test fixtures.

Every test that needs an Employee also needs a Company. Every test that needs a Company also needs a list of Departments. Every test that needs Departments also needs Roles and Permissions. You end up rebuilding the same data pyramid over and over, or worse, you share mutable state across tests and spend hours debugging why test 47 fails when you run the full suite but passes alone.

Environments fix this by treating your test data the way you treat your code: as a hierarchy.

An Environment is a plain Java class that sets up data. And just like a class can extend a superclass, an Environment can declare a parent. The parent runs first. The child trusts that the parent's data already exists, and builds on top of it.

`CompanyEnvironment` creates a company. `EmployeeEnvironment` declares `CompanyEnvironment` as its parent and creates employees. It never touches the company, because it trusts the parent already did that. `ProductEnvironment` also declares `CompanyEnvironment` as its parent and creates products. Same company, completely different branch.

What you get is a tree of environments that mirrors your domain. Each environment runs exactly once. Tests declare which environment they need, and Simulatest figures out the rest: the ordering, the dependencies, the execution.

And here's where the Insistence Layer kicks in. After each environment runs, a checkpoint is pushed. After its tests finish, the checkpoint is popped. Sibling environments never see each other's data. Individual tests at the same level are isolated from each other. At the end of the suite, the entire relational database is rolled back to its original state.

No cleanup code. No teardown. No `DELETE FROM`. No `@After`. Nothing.

And because environments are just Java classes that populate a relational database, they're not limited to tests. Got an empty local relational database and want to explore the app? Run a few environments to seed it with realistic, domain-accurate data. When you're done exploring, roll it all back.

---

## Two Tools, One Toolkit

The Insistence Layer and Environments are independent. You can use the Insistence Layer without environments: just `+1`, do work, `-1`. You can use environments without the Insistence Layer: just composable test fixtures. But together, they eliminate the two biggest time sinks in database-heavy projects: setting up test data and cleaning it up.

Environments define *what* data to create. The Insistence Layer sandbox ensures *none of it persists*.

---

*This project was inspired by an existing Smalltalk implementation from Objective Solutions.*
