package com.oscardm22.estuguia.data.datasources.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.oscardm22.estuguia.data.model.ScheduleDto
import com.oscardm22.estuguia.domain.models.Turn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreScheduleDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private companion object {
        const val SCHEDULES_COLLECTION = "schedules"
    }

    suspend fun addSchedule(schedule: ScheduleDto): Result<Boolean> = try {
        val document = if (schedule.id.isEmpty()) {
            firestore.collection(SCHEDULES_COLLECTION).document()
        } else {
            firestore.collection(SCHEDULES_COLLECTION).document(schedule.id)
        }

        val scheduleWithId = schedule.copy(id = document.id)
        document.set(scheduleWithId).await()
        Result.success(true)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getSchedules(userId: String): Result<List<ScheduleDto>> = try {
        val querySnapshot = firestore.collection(SCHEDULES_COLLECTION)
            .whereEqualTo("userId", userId)
            .orderBy("dayOfWeek")
            .orderBy("turn")
            .orderBy("startTime")
            .get()
            .await()

        val schedules = querySnapshot.documents.mapNotNull { document ->
            document.toObject(ScheduleDto::class.java)
        }
        Result.success(schedules)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getSchedulesByDay(userId: String, dayOfWeek: Int): Result<List<ScheduleDto>> = try {
        val querySnapshot = firestore.collection(SCHEDULES_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("dayOfWeek", dayOfWeek)
            .orderBy("startTime")
            .get()
            .await()

        val schedules = querySnapshot.documents.mapNotNull { document ->
            document.toObject(ScheduleDto::class.java)
        }
        Result.success(schedules)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getSchedulesByDayAndTurn(userId: String, dayOfWeek: Int, turn: Turn): Result<List<ScheduleDto>> = try {
        val querySnapshot = firestore.collection(SCHEDULES_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("dayOfWeek", dayOfWeek)
            .whereEqualTo("turn", turn)
            .orderBy("startTime")
            .get()
            .await()

        val schedules = querySnapshot.documents.mapNotNull { document ->
            document.toObject(ScheduleDto::class.java)
        }
        Result.success(schedules)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getSchedulesByTurn(userId: String, turn: Turn): Result<List<ScheduleDto>> = try {
        val querySnapshot = firestore.collection(SCHEDULES_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("turn", turn)
            .orderBy("dayOfWeek")
            .orderBy("startTime")
            .get()
            .await()

        val schedules = querySnapshot.documents.mapNotNull { document ->
            document.toObject(ScheduleDto::class.java)
        }
        Result.success(schedules)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateSchedule(schedule: ScheduleDto): Result<Boolean> = try {
        firestore.collection(SCHEDULES_COLLECTION)
            .document(schedule.id)
            .set(schedule)
            .await()
        Result.success(true)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteSchedule(scheduleId: String): Result<Boolean> = try {
        firestore.collection(SCHEDULES_COLLECTION)
            .document(scheduleId)
            .delete()
            .await()
        Result.success(true)
    } catch (e: Exception) {
        Result.failure(e)
    }
}