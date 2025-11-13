package com.oscardm22.estuguia.domain.repositories

import com.oscardm22.estuguia.domain.models.Schedule
import com.oscardm22.estuguia.domain.models.Turn

interface ScheduleRepository {
    suspend fun addSchedule(schedule: Schedule): Result<Boolean>
    suspend fun getSchedules(userId: String): Result<List<Schedule>>
    suspend fun getSchedulesByDay(userId: String, dayOfWeek: Int): Result<List<Schedule>>
    suspend fun getSchedulesByTurn(userId: String, turn: Turn): Result<List<Schedule>>
    suspend fun getSchedulesByDayAndTurn(userId: String, dayOfWeek: Int, turn: Turn): Result<List<Schedule>>
    suspend fun updateSchedule(schedule: Schedule): Result<Boolean>
    suspend fun deleteSchedule(scheduleId: String): Result<Boolean>
}