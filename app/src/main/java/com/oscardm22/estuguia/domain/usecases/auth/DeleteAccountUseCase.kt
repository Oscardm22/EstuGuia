package com.oscardm22.estuguia.domain.usecases.auth

import android.content.Context
import androidx.work.WorkManager
import com.oscardm22.estuguia.domain.repositories.AuthRepository
import com.oscardm22.estuguia.domain.repositories.ScheduleRepository
import com.oscardm22.estuguia.domain.repositories.TaskRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val scheduleRepository: ScheduleRepository,
    private val taskRepository: TaskRepository,
    private val context: Context
) {
    suspend operator fun invoke(): Result<Boolean> = try {
        // 1. Obtener el userId actual
        val userId = authRepository.getCurrentUserId()

        // 2. Eliminar TODOS los datos del usuario si existe userId
        if (userId != null) {
            // Eliminar horarios
            scheduleRepository.deleteAllSchedulesByUserId(userId)

            // Eliminar tareas
            taskRepository.deleteAllTasksByUserId(userId)

            // Cancelar todas las notificaciones pendientes
            cancelAllPendingNotifications()
        }

        // 3. Finalmente eliminar la cuenta de autenticación
        authRepository.deleteAccount()
        Result.success(true)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun cancelAllPendingNotifications() {
        // Cancelar todas las notificaciones programadas en WorkManager
        WorkManager.getInstance(context).cancelAllWork()
    }
}