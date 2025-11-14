package com.oscardm22.estuguia.domain.usecases.tasks

import com.oscardm22.estuguia.domain.models.Task
import com.oscardm22.estuguia.domain.models.TaskStatus
import com.oscardm22.estuguia.domain.repositories.TaskRepository
import javax.inject.Inject

class GetTasksByStatusUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(status: TaskStatus): Result<List<Task>> {
        return taskRepository.getTasksByStatus(status)
    }
}