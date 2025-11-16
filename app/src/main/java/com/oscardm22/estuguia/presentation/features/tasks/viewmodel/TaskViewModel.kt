package com.oscardm22.estuguia.presentation.features.tasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oscardm22.estuguia.domain.models.Schedule
import com.oscardm22.estuguia.domain.models.Task
import com.oscardm22.estuguia.domain.models.TaskStatus
import com.oscardm22.estuguia.domain.usecases.schedule.GetSchedulesUseCase
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
    private val getUpcomingTasksUseCase: GetUpcomingTasksUseCase,
    private val getSchedulesUseCase: GetSchedulesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TaskState())
    val state: StateFlow<TaskState> = _state.asStateFlow()

    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules: StateFlow<List<Schedule>> = _schedules.asStateFlow()

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

    fun loadSchedules(userId: String) {
        viewModelScope.launch {
            try {
                val result = getSchedulesUseCase(userId)

                if (result.isSuccess) {
                    val schedulesList = result.getOrThrow()
                    _schedules.value = schedulesList
                } else {
                    val mockSchedules = getMockSchedules()
                    _schedules.value = mockSchedules
                }
            } catch (e: Exception) {
                val mockSchedules = getMockSchedules()
                _schedules.value = mockSchedules
            }
        }
    }

    // Datos mock para testing
    private fun getMockSchedules(): List<Schedule> {
        return listOf(
            Schedule(
                id = "1",
                courseName = "Matemáticas Avanzadas",
                dayOfWeek = 1,
                startTime = "08:00",
                endTime = "10:00",
                classroom = "Aula 101",
                professor = "Dr. García"
            ),
            Schedule(
                id = "2",
                courseName = "Programación Android",
                dayOfWeek = 3,
                startTime = "10:00",
                endTime = "12:00",
                classroom = "Lab 202",
                professor = "Ing. Pérez"
            ),
            Schedule(
                id = "3",
                courseName = "Base de Datos",
                dayOfWeek = 5,
                startTime = "14:00",
                endTime = "16:00",
                classroom = "Aula 303",
                professor = "Lic. Rodríguez"
            )
        )
    }

    fun addTask(task: Task, userId: String) {
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val result = addTaskUseCase(task.copy(id = ""), userId)

                if (result.isSuccess) {
                    loadTasks(userId)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Error adding task"
                    _state.update { it.copy(
                        error = error,
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
                val result = updateTaskUseCase(task, userId)
                if (result.isSuccess) {
                    loadTasks(userId)
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
                    loadTasks(userId)
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
                    getTasksByStatusUseCase(status, userId)
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

    fun getTasksByStatus(userId: String, status: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                // Convertir el string a TaskStatus
                val taskStatus = when (status) {
                    "pending" -> TaskStatus.PENDING
                    "in_progress" -> TaskStatus.IN_PROGRESS
                    "completed" -> TaskStatus.COMPLETED
                    else -> null
                }

                if (taskStatus != null) {
                    val result = getTasksByStatusUseCase(taskStatus, userId)
                    if (result.isSuccess) {
                        _state.update { it.copy(
                            tasks = result.getOrThrow(),
                            isLoading = false,
                            error = null
                        ) }
                    } else {
                        _state.update { it.copy(
                            error = result.exceptionOrNull()?.message ?: "Error filtering tasks",
                            isLoading = false
                        ) }
                    }
                } else {
                    // Si no hay status válido, cargar todas las tareas
                    loadTasks(userId)
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message ?: "Unknown error",
                    isLoading = false
                ) }
            }
        }
    }
}