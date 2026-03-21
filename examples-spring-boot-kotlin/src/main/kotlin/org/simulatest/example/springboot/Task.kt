package org.simulatest.example.springboot

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository

@Entity
class Task(
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long = 0,
	var title: String = "",
	var done: Boolean = false
)

interface TaskRepository : JpaRepository<Task, Long> {
	fun findByDone(done: Boolean): List<Task>
	fun countByDone(done: Boolean): Long
}
