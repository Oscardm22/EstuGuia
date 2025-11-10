package com.oscardm22.estuguia.presentation.features.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import com.oscardm22.estuguia.domain.usecases.LoginUseCase
import com.oscardm22.estuguia.domain.usecases.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    // 🎯 StateFlow para Compose - REVERTIDO A StateFlow
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterState())
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 🎯 LiveData adapters para Activities nativas - NUEVO
    val loginStateLiveData: LiveData<LoginState> = _loginState.asLiveData()
    val isLoadingLiveData: LiveData<Boolean> = _isLoading.asLiveData()

    // 🔐 Métodos de Login
    fun login(email: String, password: String) {
        _isLoading.value = true
        _loginState.value = LoginState(isLoading = true)

        viewModelScope.launch {
            val result = loginUseCase(email, password)
            _isLoading.value = false

            result.fold(
                onSuccess = { user ->
                    _loginState.value = LoginState(
                        isSuccess = true,
                        user = user
                    )
                },
                onFailure = { error ->
                    _loginState.value = LoginState(
                        isError = true,
                        errorMessage = error.message ?: "Error desconocido en login"
                    )
                }
            )
        }
    }

    // 📝 Métodos de Registro
    fun register(
        email: String,
        password: String,
        name: String,
        grade: String,
        section: String? = null,
        school: String? = null
    ) {
        _isLoading.value = true
        _registerState.value = RegisterState(isLoading = true)

        viewModelScope.launch {
            val result = registerUseCase(email, password, name, grade, section, school)
            _isLoading.value = false

            result.fold(
                onSuccess = { user ->
                    _registerState.value = RegisterState(
                        isSuccess = true,
                        user = user
                    )
                },
                onFailure = { error ->
                    _registerState.value = RegisterState(
                        isError = true,
                        errorMessage = error.message ?: "Error desconocido en registro"
                    )
                }
            )
        }
    }

    // 🧹 Métodos para limpiar estados
    fun clearLoginState() {
        _loginState.value = LoginState()
    }

    fun clearRegisterState() {
        _registerState.value = RegisterState()
    }

    // 🔄 Resetear todos los estados
    fun resetAllStates() {
        _loginState.value = LoginState()
        _registerState.value = RegisterState()
        _isLoading.value = false
    }
}