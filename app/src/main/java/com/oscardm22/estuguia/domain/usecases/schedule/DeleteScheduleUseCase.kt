package com.oscardm22.estuguia.domain.usecases.schedule

import com.oscardm22.estuguia.domain.repositories.ScheduleRepository
import javax.inject.Inject

class DeleteScheduleUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) {
    suspend operator fun invoke(scheduleId: String): Result<Boolean> {
        return scheduleRepository.deleteSchedule(scheduleId)
    }
}