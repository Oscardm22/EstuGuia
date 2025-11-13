package com.oscardm22.estuguia.domain.usecases.schedule

import com.oscardm22.estuguia.domain.models.Schedule
import com.oscardm22.estuguia.domain.repositories.ScheduleRepository
import javax.inject.Inject

class GetSchedulesUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) {
    suspend operator fun invoke(userId: String): Result<List<Schedule>> {
        return scheduleRepository.getSchedules(userId)
    }
}