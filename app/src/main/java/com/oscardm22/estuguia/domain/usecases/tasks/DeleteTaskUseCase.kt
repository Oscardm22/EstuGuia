package com.oscardm22.estuguia.domain.usecases.tasks

import com.oscardm22.estuguia.domain.repositories.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: String): Result<Boolean> {
        return taskRepository.deleteTask(taskId)
    }
}