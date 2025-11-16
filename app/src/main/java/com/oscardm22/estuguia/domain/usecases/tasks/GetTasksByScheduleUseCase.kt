package com.oscardm22.estuguia.domain.usecases.tasks

import com.oscardm22.estuguia.domain.models.Task
import com.oscardm22.estuguia.domain.repositories.TaskRepository
import javax.inject.Inject

class GetTasksByScheduleUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(scheduleId: String, userId: String): Result<List<Task>> {
        return taskRepository.getTasksBySchedule(scheduleId, userId)
    }
}