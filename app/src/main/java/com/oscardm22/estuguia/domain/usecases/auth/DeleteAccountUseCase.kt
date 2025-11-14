package com.oscardm22.estuguia.domain.usecases.auth

import com.oscardm22.estuguia.domain.repositories.AuthRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Boolean> {
        return authRepository.deleteAccount()
    }
}