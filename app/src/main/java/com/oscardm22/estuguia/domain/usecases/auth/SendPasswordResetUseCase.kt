package com.oscardm22.estuguia.domain.usecases.auth

import com.oscardm22.estuguia.domain.repositories.AuthRepository
import javax.inject.Inject

class SendPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Boolean> {
        return try {
            authRepository.sendPasswordResetEmail(email)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}