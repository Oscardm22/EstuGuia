package com.oscardm22.estuguia.data.datasources.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.oscardm22.estuguia.data.model.TaskDto
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class FirestoreTaskDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private companion object {
        const val TASKS_COLLECTION = "tasks"
    }

    suspend fun addTask(task: TaskDto): String {
        val documentRef = firestore.collection(TASKS_COLLECTION).document()
        val taskWithId = task.copy(id = documentRef.id)
        documentRef.set(taskWithId).await()
        return documentRef.id
    }

    suspend fun getTasks(userId: String): List<TaskDto> {
        return firestore.collection(TASKS_COLLECTION)
            .whereEqualTo("userId", userId)
            .orderBy("dueDate", Query.Direction.ASCENDING)
            .get()
            .await()
            .toObjects(TaskDto::class.java)
    }

    suspend fun getTaskById(taskId: String): TaskDto? {
        return firestore.collection(TASKS_COLLECTION)
            .document(taskId)
            .get()
            .await()
            .toObject(TaskDto::class.java)
    }

    suspend fun updateTask(task: TaskDto): Boolean {
        return try {
            firestore.collection(TASKS_COLLECTION)
                .document(task.id)
                .set(task)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteTask(taskId: String): Boolean {
        return try {
            firestore.collection(TASKS_COLLECTION)
                .document(taskId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getTasksBySchedule(userId: String, scheduleId: String): List<TaskDto> {
        return firestore.collection(TASKS_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("scheduleId", scheduleId)
            .orderBy("dueDate", Query.Direction.ASCENDING)
            .get()
            .await()
            .toObjects(TaskDto::class.java)
    }

    suspend fun getTasksByStatus(userId: String, status: String): List<TaskDto> {
        return firestore.collection(TASKS_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", status)
            .orderBy("dueDate", Query.Direction.ASCENDING)
            .get()
            .await()
            .toObjects(TaskDto::class.java)
    }

    suspend fun getUpcomingTasks(userId: String, days: Int): List<TaskDto> {
        val startDate = Date()
        val endDate = Date(startDate.time + days * 24 * 60 * 60 * 1000L)

        return firestore.collection(TASKS_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("dueDate", startDate)
            .whereLessThanOrEqualTo("dueDate", endDate)
            .orderBy("dueDate", Query.Direction.ASCENDING)
            .get()
            .await()
            .toObjects(TaskDto::class.java)
    }

    suspend fun getTasksByDateRange(userId: String, startDate: Date, endDate: Date): List<TaskDto> {
        return firestore.collection(TASKS_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("dueDate", startDate)
            .whereLessThanOrEqualTo("dueDate", endDate)
            .orderBy("dueDate", Query.Direction.ASCENDING)
            .get()
            .await()
            .toObjects(TaskDto::class.java)
    }

    suspend fun getPendingTasksCount(userId: String): Int {
        return try {
            val query = firestore.collection(TASKS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "PENDING")
                .get()
                .await()

            query.documents.size
        } catch (e: Exception) {
            // Log del error para debugging
            println("Error getting pending tasks count: ${e.message}")
            0
        }
    }
}