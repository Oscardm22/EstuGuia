package com.oscardm22.estuguia.domain.usecases.tasks

import com.oscardm22.estuguia.domain.models.Task
import com.oscardm22.estuguia.domain.repositories.TaskRepository
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Result<Boolean> {
        return taskRepository.updateTask(task)
    }
}