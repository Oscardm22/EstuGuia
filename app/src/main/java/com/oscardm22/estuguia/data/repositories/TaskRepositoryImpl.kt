package com.oscardm22.estuguia.data.repositories

import com.oscardm22.estuguia.data.datasources.remote.FirestoreTaskDataSource
import com.oscardm22.estuguia.data.model.TaskDto
import com.oscardm22.estuguia.domain.models.Task
import com.oscardm22.estuguia.domain.models.TaskPriority
import com.oscardm22.estuguia.domain.models.TaskStatus
import com.oscardm22.estuguia.domain.repositories.TaskRepository
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDataSource: FirestoreTaskDataSource
) : TaskRepository {

    override suspend fun addTask(task: Task, userId: String): Result<String> {
        return try {
            val taskDto = task.toDto(userId)
            val taskId = taskDataSource.addTask(taskDto)
            Result.success(taskId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTasks(userId: String): Result<List<Task>> {
        return try {
            val tasksDto = taskDataSource.getTasks(userId)
            val tasks = tasksDto.map { it.toDomain() }
            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTaskById(taskId: String): Result<Task> {
        return try {
            val taskDto = taskDataSource.getTaskById(taskId)
            if (taskDto != null) {
                Result.success(taskDto.toDomain())
            } else {
                Result.failure(Exception("Task not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTask(task: Task, userId: String): Result<Boolean> {
        return try {
            val taskDto = task.toDto(userId)
            val success = taskDataSource.updateTask(taskDto)
            if (success) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to update task"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Boolean> {
        return try {
            val success = taskDataSource.deleteTask(taskId)
            if (success) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to delete task"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTasksBySchedule(scheduleId: String, userId: String): Result<List<Task>> {
        return try {
            val tasksDto = taskDataSource.getTasksBySchedule(userId, scheduleId)
            val tasks = tasksDto.map { it.toDomain() }
            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTasksByStatus(status: TaskStatus, userId: String): Result<List<Task>> {
        return try {
            val tasksDto = taskDataSource.getTasksByStatus(userId, status.name)
            val tasks = tasksDto.map { it.toDomain() }
            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUpcomingTasks(userId: String, days: Int): Result<List<Task>> {
        return try {
            val tasksDto = taskDataSource.getUpcomingTasks(userId, days)
            val tasks = tasksDto.map { it.toDomain() }
            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPendingTasksCount(userId: String): Int {
        return taskDataSource.getPendingTasksCount(userId)
    }

    override suspend fun deleteAllTasksByUserId(userId: String) {
        taskDataSource.deleteAllTasksByUserId(userId)
    }

    // Extension functions para conversión
    private fun Task.toDto(userId: String): TaskDto {
        return TaskDto(
            id = id,
            title = title,
            description = description,
            scheduleId = scheduleId,
            dueDate = dueDate,
            priority = priority.name,
            status = status.name,
            reminderTime = reminderTime,
            createdAt = createdAt,
            updatedAt = updatedAt,
            userId = userId
        )
    }

    private fun TaskDto.toDomain(): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            scheduleId = scheduleId,
            dueDate = dueDate,
            priority = TaskPriority.valueOf(priority),
            status = TaskStatus.valueOf(status),
            reminderTime = reminderTime,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}