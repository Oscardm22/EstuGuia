package com.oscardm22.estuguia.domain.usecases.schedule

import com.oscardm22.estuguia.domain.models.Schedule
import com.oscardm22.estuguia.domain.repositories.ScheduleRepository
import javax.inject.Inject

class UpdateScheduleUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) {
    suspend operator fun invoke(schedule: Schedule): Result<Boolean> {
        return scheduleRepository.updateSchedule(schedule)
    }
}