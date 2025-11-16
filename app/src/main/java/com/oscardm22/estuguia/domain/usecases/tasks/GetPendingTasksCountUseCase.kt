package com.oscardm22.estuguia.domain.usecases.tasks

import com.oscardm22.estuguia.domain.repositories.TaskRepository
import javax.inject.Inject

class GetPendingTasksCountUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(userId: String): Result<Int> {
        return try {
            val count = taskRepository.getPendingTasksCount(userId)
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}