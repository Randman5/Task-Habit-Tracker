package com.pet.data.repository

import android.util.Log
import com.pet.domain.models.Task
import kotlinx.coroutines.delay


class TasksRepository {

    // TODO: временно пока нет бд
    private val tasks = mutableListOf<Task>()

    // TODO: временно для генерации id
    private var idCounter = 0

    init {
        repeat(20) {
            tasks.add(Task(
                id = it,
                name = "Задача $it",
                checked = false
            ))
            idCounter++
        }
    }

    suspend fun getTasks(): List<Task> {
        delay(250)
        Log.d("TASKS", "$tasks")
        return tasks
    }

    suspend fun add(task: String) {
        delay(100)
        tasks.add(Task(
            id = idCounter,
            name = task,
            checked = false
        ))
        idCounter++
    }

    suspend fun edit(task: Task) {
        delay(100)
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = Task(
                id = task.id,
                name = task.name,
                checked = task.checked
            )
        }
    }

    suspend fun delete(task: Task) {
        delay(100)
        tasks.removeIf { it.id == task.id }
    }
}