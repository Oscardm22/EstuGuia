package com.oscardm22.estuguia.domain.usecases.auth

import com.oscardm22.estuguia.domain.models.User
import com.oscardm22.estuguia.domain.repositories.AuthRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(user: User): Result<Boolean> {
        return authRepository.updateProfile(user)
    }
}