package com.oscardm22.estuguia.presentation.features.tasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oscardm22.estuguia.domain.models.Task
import com.oscardm22.estuguia.domain.models.TaskStatus
import com.oscardm22.estuguia.domain.usecases.tasks.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getTasksByScheduleUseCase: GetTasksByScheduleUseCase,
    private val getTasksByStatusUseCase: GetTasksByStatusUseCase,
    private val getUpcomingTasksUseCase: GetUpcomingTasksUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TaskState())
    val state: StateFlow<TaskState> = _state.asStateFlow()

    fun loadTasks(userId: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val result = getTasksUseCase(userId)
                if (result.isSuccess) {
                    _state.update { it.copy(
                        tasks = result.getOrThrow(),
                        isLoading = false
                    ) }
                } else {
                    _state.update { it.copy(
                        error = result.exceptionOrNull()?.message ?: "Error loading tasks",
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message ?: "Unknown error",
                    isLoading = false
                ) }
            }
        }
    }

    fun addTask(task: Task, userId: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val result = addTaskUseCase(task.copy(id = ""))
                if (result.isSuccess) {
                    loadTasks(userId) // Recargar la lista
                } else {
                    _state.update { it.copy(
                        error = result.exceptionOrNull()?.message ?: "Error adding task",
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message ?: "Unknown error",
                    isLoading = false
                ) }
            }
        }
    }

    fun updateTask(task: Task, userId: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val result = updateTaskUseCase(task)
                if (result.isSuccess) {
                    loadTasks(userId) // Recargar la lista
                } else {
                    _state.update { it.copy(
                        error = result.exceptionOrNull()?.message ?: "Error updating task",
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message ?: "Unknown error",
                    isLoading = false
                ) }
            }
        }
    }

    fun deleteTask(taskId: String, userId: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val result = deleteTaskUseCase(taskId)
                if (result.isSuccess) {
                    loadTasks(userId) // Recargar la lista
                } else {
                    _state.update { it.copy(
                        error = result.exceptionOrNull()?.message ?: "Error deleting task",
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message ?: "Unknown error",
                    isLoading = false
                ) }
            }
        }
    }

    fun selectTask(task: Task?) {
        _state.update { it.copy(selectedTask = task) }
    }

    fun filterByStatus(status: TaskStatus?, userId: String) {
        _state.update { it.copy(filterStatus = status?.name, isLoading = true) }
        viewModelScope.launch {
            try {
                val result = if (status != null) {
                    getTasksByStatusUseCase(status)
                } else {
                    getTasksUseCase(userId)
                }

                if (result.isSuccess) {
                    _state.update { it.copy(
                        tasks = result.getOrThrow(),
                        isLoading = false
                    ) }
                } else {
                    _state.update { it.copy(
                        error = result.exceptionOrNull()?.message ?: "Error filtering tasks",
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message ?: "Unknown error",
                    isLoading = false
                ) }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}