package com.oscardm22.estuguia.domain.usecases.auth

import com.oscardm22.estuguia.domain.models.User
import com.oscardm22.estuguia.domain.repositories.AuthRepository
import javax.inject.Inject

/**
 * Caso de uso para el proceso de inicio de sesión
 * Contiene la lógica de negocio específica para el login
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    /**
     * Ejecuta el proceso completo de login
     * @return Result con el usuario autenticado o error
     */
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // 1. Validar campos de entrada
        val emailValidation = validateEmail(email)
        if (!emailValidation.isValid) {
            return Result.failure(IllegalArgumentException(emailValidation.errorMessage))
        }

        val passwordValidation = validatePassword(password)
        if (!passwordValidation.isValid) {
            return Result.failure(IllegalArgumentException(passwordValidation.errorMessage))
        }

        // 2. Intentar autenticación con el repositorio
        return authRepository.login(email, password)
    }

    /**
     * Valida formato de email específico para estudiantes
     */
    private fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult(
                isValid = false,
                errorMessage = "El email no puede estar vacío"
            )
            !email.contains("@") -> ValidationResult(
                isValid = false,
                errorMessage = "El formato del email no es válido"
            )
            else -> ValidationResult(isValid = true)
        }
    }

    /**
     * Valida contraseña según requisitos de seguridad básicos
     */
    private fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult(
                isValid = false,
                errorMessage = "La contraseña no puede estar vacía"
            )
            password.length < 6 -> ValidationResult(
                isValid = false,
                errorMessage = "La contraseña debe tener al menos 6 caracteres"
            )
            else -> ValidationResult(isValid = true)
        }
    }

    /**
      Verifica si hay un usuario autenticado actualmente
     */
    suspend fun isUserAlreadyLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }
}

/**
 * Clase para representar resultado de validaciones
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)