package com.oscardm22.estuguia.presentation.features.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oscardm22.estuguia.domain.models.User
import com.oscardm22.estuguia.domain.usecases.auth.DeleteAccountUseCase
import com.oscardm22.estuguia.domain.usecases.auth.GetCurrentUserProfileUseCase
import com.oscardm22.estuguia.domain.usecases.auth.LogoutUseCase
import com.oscardm22.estuguia.domain.usecases.auth.UpdatePasswordUseCase
import com.oscardm22.estuguia.domain.usecases.auth.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserProfileUseCase: GetCurrentUserProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    fun loadUserProfile() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val result = getCurrentUserProfileUseCase()
                if (result.isSuccess) {
                    _state.update { it.copy(
                        user = result.getOrThrow(),
                        isLoading = false
                    ) }
                } else {
                    _state.update { it.copy(
                        error = result.exceptionOrNull()?.message ?: "Error cargando perfil",
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message ?: "Error desconocido",
                    isLoading = false
                ) }
            }
        }
    }

    fun updateProfile(user: User) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val result = updateProfileUseCase(user)
                if (result.isSuccess) {
                    _state.update { it.copy(
                        user = user,
                        isLoading = false,
                        isEditing = false
                    ) }
                } else {
                    _state.update { it.copy(
                        error = result.exceptionOrNull()?.message ?: "Error actualizando perfil",
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message ?: "Error desconocido",
                    isLoading = false
                ) }
            }
        }
    }

    fun updatePassword(currentPassword: String, newPassword: String) {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val result = updatePasswordUseCase(currentPassword, newPassword)
                if (result.isSuccess) {
                    _state.update { it.copy(
                        isLoading = false,
                        showPasswordDialog = false
                    ) }
                } else {
                    _state.update { it.copy(
                        error = result.exceptionOrNull()?.message ?: "Error actualizando contraseña",
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message ?: "Error desconocido",
                    isLoading = false
                ) }
            }
        }
    }

    fun logout() {
        _state.update { it.copy(isLoggingOut = true) }
        viewModelScope.launch {
            try {
                logoutUseCase()
                _state.update { it.copy(isLoggingOut = false) }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = "Error durante el cierre de sesión",
                    isLoggingOut = false
                ) }
            }
        }
    }

    fun deleteAccount() {
        _state.update { it.copy(isDeletingAccount = true) }
        viewModelScope.launch {
            try {
                val result = deleteAccountUseCase()
                if (result.isSuccess) {
                    _state.update { it.copy(isDeletingAccount = false) }
                } else {
                    _state.update { it.copy(
                        error = result.exceptionOrNull()?.message ?: "Error eliminando cuenta",
                        isDeletingAccount = false
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message ?: "Error desconocido",
                    isDeletingAccount = false
                ) }
            }
        }
    }

    fun setEditing(isEditing: Boolean) {
        _state.update { it.copy(isEditing = isEditing) }
    }

    fun setShowPasswordDialog(show: Boolean) {
        _state.update { it.copy(showPasswordDialog = show) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}