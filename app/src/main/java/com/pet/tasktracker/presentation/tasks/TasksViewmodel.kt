package com.pet.tasktracker.presentation.tasks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pet.data.repository.TasksRepository
import kotlinx.coroutines.launch

class TasksViewmodel: ViewModel() {

    val tasks = MutableLiveData<List<String>>()

    val taskRepository = TasksRepository()

    fun loadTasks() {
        viewModelScope.launch {
            launch {
                tasks.postValue(taskRepository.getTasks())
            }
        }
    }
}