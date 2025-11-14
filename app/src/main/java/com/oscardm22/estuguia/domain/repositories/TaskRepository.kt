package com.oscardm22.estuguia.domain.repositories

import com.oscardm22.estuguia.domain.models.Task
import com.oscardm22.estuguia.domain.models.TaskStatus

interface TaskRepository {
    suspend fun addTask(task: Task): Result<String>
    suspend fun getTasks(userId: String): Result<List<Task>>
    suspend fun getTaskById(taskId: String): Result<Task>
    suspend fun updateTask(task: Task): Result<Boolean>
    suspend fun deleteTask(taskId: String): Result<Boolean>
    suspend fun getTasksBySchedule(scheduleId: String): Result<List<Task>>
    suspend fun getTasksByStatus(status: TaskStatus): Result<List<Task>>
    suspend fun getUpcomingTasks(userId: String, days: Int): Result<List<Task>>
}