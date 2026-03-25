package com.pet.data.repository

import kotlinx.coroutines.delay


class TasksRepository {

    suspend fun getTasks(): List<String> {
        delay(2000)
        return List(100) {
            "Задача $it"
        }
    }
}