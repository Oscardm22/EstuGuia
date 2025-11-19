package com.oscardm22.estuguia.presentation.features.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.oscardm22.estuguia.domain.usecases.auth.LoginUseCase
import com.oscardm22.estuguia.domain.usecases.auth.RegisterUseCase
import com.oscardm22.estuguia.domain.usecases.auth.SendPasswordResetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val sendPasswordResetUseCase: SendPasswordResetUseCase
) : ViewModel() {
    // LiveData para estados de Login y Registro
    private val _loginState = MutableLiveData(LoginState())
    val loginStateLiveData: LiveData<LoginState> = _loginState

    private val _registerState = MutableLiveData(RegisterState())
    val registerStateLiveData: LiveData<RegisterState> = _registerState

    private val _isLoading = MutableLiveData(false)
    val isLoadingLiveData: LiveData<Boolean> = _isLoading

    // Métodos de Login
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
                    val errorMessage = handleLoginError(error)
                    _loginState.value = LoginState(
                        isError = true,
                        errorMessage = errorMessage
                    )
                }
            )
        }
    }

    // Métodos de Registro
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
                    val errorMessage = handleRegisterError(error)
                    _registerState.value = RegisterState(
                        isError = true,
                        errorMessage = errorMessage
                    )
                }
            )
        }
    }

    private fun handleLoginError(exception: Throwable): String {
        return when (exception) {
            is FirebaseAuthInvalidUserException -> "No existe una cuenta con este correo electrónico"
            is FirebaseAuthInvalidCredentialsException -> "Contraseña incorrecta"
            else -> "Error al iniciar sesión. Por favor verifica tus credenciales"
        }
    }

    private fun handleRegisterError(exception: Throwable): String {
        return when (exception) {
            is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con este correo electrónico"
            is FirebaseAuthInvalidCredentialsException -> "La contraseña debe tener al menos 6 caracteres"
            else -> "Error al crear la cuenta. Por favor intenta nuevamente"
        }
    }

    fun sendPasswordResetEmail(email: String, callback: (Result<Boolean>) -> Unit) {
        viewModelScope.launch {
            val result = sendPasswordResetUseCase(email)
            callback(result)
        }
    }
}