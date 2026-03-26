package com.pet.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class TasksRepository {

    private val tasks = mutableListOf<String>()
    init {
        repeat(20) {
            tasks.add("Задача $it")
        }
    }

    suspend fun getTasks(): List<String> {
        delay(250)
        return tasks
    }

    suspend fun add(task: String) {
        delay(100)
        tasks.add(task)
    }

    suspend fun delete(task: String) {
        delay(100)
        tasks.remove(task)
    }
}