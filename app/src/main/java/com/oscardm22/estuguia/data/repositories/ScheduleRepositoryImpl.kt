package com.oscardm22.estuguia.data.repositories

import com.oscardm22.estuguia.data.datasources.remote.FirestoreScheduleDataSource
import com.oscardm22.estuguia.domain.models.Schedule
import com.oscardm22.estuguia.domain.models.Turn
import com.oscardm22.estuguia.domain.repositories.ScheduleRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.map

@Singleton
class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleDataSource: FirestoreScheduleDataSource
) : ScheduleRepository {

    override suspend fun addSchedule(schedule: Schedule): Result<Boolean> {
        return scheduleDataSource.addSchedule(schedule.toDto())
    }

    override suspend fun getSchedules(userId: String): Result<List<Schedule>> {
        return scheduleDataSource.getSchedules(userId).map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getSchedulesByDay(userId: String, dayOfWeek: Int): Result<List<Schedule>> {
        return scheduleDataSource.getSchedulesByDay(userId, dayOfWeek).map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getSchedulesByTurn(userId: String, turn: Turn): Result<List<Schedule>> {
        return scheduleDataSource.getSchedulesByTurn(userId, turn).map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getSchedulesByDayAndTurn(userId: String, dayOfWeek: Int, turn: Turn): Result<List<Schedule>> {
        return scheduleDataSource.getSchedulesByDayAndTurn(userId, dayOfWeek, turn).map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun updateSchedule(schedule: Schedule): Result<Boolean> {
        return scheduleDataSource.updateSchedule(schedule.toDto())
    }

    override suspend fun deleteSchedule(scheduleId: String): Result<Boolean> {
        return scheduleDataSource.deleteSchedule(scheduleId)
    }

    override suspend fun deleteAllSchedulesByUserId(userId: String) {
        scheduleDataSource.deleteAllSchedulesByUserId(userId)
    }
}