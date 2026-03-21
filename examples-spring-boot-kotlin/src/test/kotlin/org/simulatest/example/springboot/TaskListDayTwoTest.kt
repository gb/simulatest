package org.simulatest.example.springboot

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.simulatest.environment.annotation.UseEnvironment
import org.springframework.beans.factory.annotation.Autowired

@UseEnvironment(TaskListDayTwoEnvironment::class)
class TaskListDayTwoTest {

	@Autowired lateinit var tasks: TaskRepository

	@Test
	fun sameTenTasksStillExist() {
		assertEquals(10, tasks.count())
	}

	@Test
	fun halfTheBoardIsDone() {
		assertEquals(5, tasks.countByDone(true))
		assertEquals(5, tasks.countByDone(false))
	}

	@Test
	fun bugFixIsDone() {
		val loginBug = tasks.findAll().first { it.title == "Fix login bug" }
		assertTrue(loginBug.done)
	}

	@Test
	fun dashboardDesignIsStillPending() {
		val dashboard = tasks.findAll().first { it.title == "Design new dashboard" }
		assertFalse(dashboard.done)
	}

	@Test
	fun completingAnotherTaskDoesNotLeakToOtherTests() {
		val dashboard = tasks.findAll().first { it.title == "Design new dashboard" }
		dashboard.done = true
		tasks.save(dashboard)

		assertEquals(6, tasks.countByDone(true))
		// rolled back — other tests still see 5 done
	}

	@Test
	fun undoingATaskDoesNotLeakEither() {
		val pipeline = tasks.findAll().first { it.title == "Set up CI pipeline" }
		pipeline.done = false
		tasks.save(pipeline)

		assertEquals(4, tasks.countByDone(true))
		// rolled back — other tests still see 5 done
	}
}
