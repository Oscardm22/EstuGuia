package com.oscardm22.estuguia.domain.usecases.tasks

import com.oscardm22.estuguia.domain.models.Task
import com.oscardm22.estuguia.domain.repositories.TaskRepository
import javax.inject.Inject

class GetUpcomingTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(userId: String, days: Int = 7): Result<List<Task>> {
        return taskRepository.getUpcomingTasks(userId, days)
    }
}