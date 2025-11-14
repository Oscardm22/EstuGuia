package com.oscardm22.estuguia.domain.usecases.auth

import com.oscardm22.estuguia.domain.models.User
import com.oscardm22.estuguia.domain.repositories.AuthRepository
import javax.inject.Inject

class GetCurrentUserProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<User> {
        return authRepository.getCurrentUserProfile()
    }
}