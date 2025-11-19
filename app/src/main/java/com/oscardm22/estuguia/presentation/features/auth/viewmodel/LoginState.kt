package com.oscardm22.estuguia.presentation.features.auth.viewmodel

import com.oscardm22.estuguia.domain.models.User

/**
 * Estado de la UI para el proceso de Login
 */
data class LoginState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val user: User? = null
)