package com.oscardm22.estuguia.domain.usecases.auth

import com.oscardm22.estuguia.domain.repositories.AuthRepository
import javax.inject.Inject

class UpdatePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(currentPassword: String, newPassword: String): Result<Boolean> {
        return authRepository.updatePassword(currentPassword, newPassword)
    }
}