package com.oscardm22.estuguia.presentation.features.profile.viewmodel

import com.oscardm22.estuguia.domain.models.User

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEditing: Boolean = false,
    val isLoggingOut: Boolean = false,
    val isDeletingAccount: Boolean = false,
    val showPasswordDialog: Boolean = false
)