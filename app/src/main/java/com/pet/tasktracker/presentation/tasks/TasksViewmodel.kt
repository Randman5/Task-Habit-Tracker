package com.pet.tasktracker.presentation.tasks

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pet.data.repository.TasksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TasksViewmodel: ViewModel() {

    val tasks = MutableLiveData<List<String>>()

    val taskRepository = TasksRepository()

    fun loadTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                tasks.postValue(taskRepository.getTasks().toList())
            } catch (e: Exception) {
                tasks.postValue(emptyList())
            }
        }
    }

    fun addTask(task: String) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.add(task)
            loadTasks()
        }
    }

    fun deleteTask(task: String) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.delete(task)
            loadTasks()
        }
    }
}