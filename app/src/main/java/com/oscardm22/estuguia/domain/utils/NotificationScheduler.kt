package com.oscardm22.estuguia.domain.utils

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Data
import com.oscardm22.estuguia.worker.NotificationWorker
import java.util.concurrent.TimeUnit
import java.util.Date

class NotificationScheduler {

    companion object {

        fun scheduleTaskReminder(
            context: Context,
            taskId: String,
            taskTitle: String,
            reminderTime: Date,
            message: String = "No olvides completar esta tarea"
        ) {
            val currentTime = System.currentTimeMillis()
            val reminderTimeMillis = reminderTime.time
            val delay = reminderTimeMillis - currentTime

            // Solo programar si el recordatorio es en el futuro
            if (delay > 0) {
                val inputData = Data.Builder()
                    .putString(NotificationWorker.KEY_TITLE, "Recordatorio: $taskTitle")
                    .putString(NotificationWorker.KEY_MESSAGE, message)
                    .putString(NotificationWorker.KEY_TASK_ID, taskId)
                    .build()

                val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .build()

                WorkManager.getInstance(context).enqueue(notificationWork)
            }
        }

        fun cancelTaskReminder(context: Context, taskId: String) {
            WorkManager.getInstance(context).cancelAllWorkByTag(taskId)
        }
    }
}