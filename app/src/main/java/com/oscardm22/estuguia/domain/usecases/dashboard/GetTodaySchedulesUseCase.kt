package com.oscardm22.estuguia.domain.usecases.dashboard

import com.oscardm22.estuguia.domain.models.Schedule
import com.oscardm22.estuguia.domain.repositories.AuthRepository
import com.oscardm22.estuguia.domain.repositories.ScheduleRepository
import java.util.Calendar
import javax.inject.Inject

class GetTodaySchedulesUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val scheduleRepository: ScheduleRepository
) {
    suspend operator fun invoke(): List<Schedule> {
        val userId = authRepository.getCurrentUserId() ?: return emptyList()
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

        val adjustedDay = when (today) {
            Calendar.SUNDAY -> 7
            else -> today - 1
        }

        return scheduleRepository.getSchedulesByDay(userId, adjustedDay)
            .getOrElse { emptyList() }
    }
}