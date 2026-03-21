package org.simulatest.example.springboot

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.simulatest.environment.annotation.UseEnvironment
import org.springframework.beans.factory.annotation.Autowired

@UseEnvironment(TaskListEnvironment::class)
class TaskListTest {

	@Autowired lateinit var tasks: TaskRepository

	@Test
	fun tenTasksOnAFreshBoard() {
		assertEquals(10, tasks.count())
	}

	@Test
	fun nothingDoneYet() {
		assertEquals(0, tasks.countByDone(true))
		assertEquals(10, tasks.countByDone(false))
	}

	@Test
	fun ciPipelineIsOnTheList() {
		val titles = tasks.findAll().map { it.title }
		assertTrue("Set up CI pipeline" in titles)
	}

	@Test
	fun addingATaskDoesNotLeakToOtherTests() {
		tasks.save(Task(title = "Sneak peek"))
		assertEquals(11, tasks.count())
		// next test still sees 10 — Insistence Layer rolls this back
	}

	@Test
	fun deletingEverythingDoesNotLeakEither() {
		tasks.deleteAll()
		assertEquals(0, tasks.count())
		// rolled back — other tests still see 10
	}
}
