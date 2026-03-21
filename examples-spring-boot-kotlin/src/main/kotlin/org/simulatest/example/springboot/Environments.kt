package org.simulatest.example.springboot

import org.simulatest.environment.Environment
import org.simulatest.environment.annotation.EnvironmentParent
import org.springframework.beans.factory.annotation.Autowired

/**
 * Environment tree:
 *
 *   TaskListEnvironment        ◄── 10 fresh tasks, none done
 *     └── TaskListDayTwoEnvironment  ◄── 5 tasks marked done
 */
class TaskListEnvironment : Environment {

	@Autowired lateinit var tasks: TaskRepository

	override fun run() {
		tasks.saveAll(listOf(
			Task(title = "Set up CI pipeline"),
			Task(title = "Write unit tests"),
			Task(title = "Review pull request"),
			Task(title = "Fix login bug"),
			Task(title = "Update dependencies"),
			Task(title = "Design new dashboard"),
			Task(title = "Refactor auth module"),
			Task(title = "Add error logging"),
			Task(title = "Write API docs"),
			Task(title = "Deploy to staging")
		))
	}
}

@EnvironmentParent(TaskListEnvironment::class)
class TaskListDayTwoEnvironment : Environment {

	@Autowired lateinit var tasks: TaskRepository

	override fun run() {
		val done = setOf(
			"Set up CI pipeline",
			"Write unit tests",
			"Fix login bug",
			"Update dependencies",
			"Add error logging"
		)
		tasks.saveAll(
			tasks.findAll()
				.filter { it.title in done }
				.onEach { it.done = true }
		)
	}
}
