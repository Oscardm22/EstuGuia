package com.oscardm22.estuguia.domain.repositories

import com.oscardm22.estuguia.domain.models.Task
import com.oscardm22.estuguia.domain.models.TaskStatus

interface TaskRepository {
    suspend fun addTask(task: Task, userId: String): Result<String>
    suspend fun getTasks(userId: String): Result<List<Task>>
    suspend fun getTaskById(taskId: String): Result<Task>
    suspend fun updateTask(task: Task, userId: String): Result<Boolean>
    suspend fun deleteTask(taskId: String): Result<Boolean>
    suspend fun getTasksBySchedule(scheduleId: String, userId: String): Result<List<Task>>
    suspend fun getTasksByStatus(status: TaskStatus, userId: String): Result<List<Task>>
    suspend fun getUpcomingTasks(userId: String, days: Int): Result<List<Task>>
}