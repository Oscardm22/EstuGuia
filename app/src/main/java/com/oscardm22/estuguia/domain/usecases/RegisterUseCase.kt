package com.oscardm22.estuguia.domain.usecases

import com.oscardm22.estuguia.domain.models.User
import com.oscardm22.estuguia.domain.repositories.AuthRepository
import com.oscardm22.estuguia.domain.utils.ValidationResult
import javax.inject.Inject

/**
 * Caso de uso para el proceso de registro de usuarios
 * Contiene la lógica de negocio específica para el registro
 */
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    /**
     * Ejecuta el proceso completo de registro
     * @return Result con el usuario creado o error
     */
    suspend operator fun invoke(
        email: String,
        password: String,
        name: String,
        grade: String,
        section: String? = null,
        school: String? = null
    ): Result<User> {
        // 1. Validar campos de entrada
        val emailValidation = validateEmail(email)
        if (!emailValidation.isValid) {
            return Result.failure(IllegalArgumentException(emailValidation.errorMessage))
        }

        val passwordValidation = validatePassword(password)
        if (!passwordValidation.isValid) {
            return Result.failure(IllegalArgumentException(passwordValidation.errorMessage))
        }

        val nameValidation = validateName(name)
        if (!nameValidation.isValid) {
            return Result.failure(IllegalArgumentException(nameValidation.errorMessage))
        }

        val gradeValidation = validateGrade(grade)
        if (!gradeValidation.isValid) {
            return Result.failure(IllegalArgumentException(gradeValidation.errorMessage))
        }

        // 2. Intentar registro con el repositorio
        return authRepository.register(email, password, name, grade, section, school)
    }

    /**
     * Valida formato de email
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
            email.length < 5 -> ValidationResult(
                isValid = false,
                errorMessage = "El email es demasiado corto"
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
     * Valida nombre del estudiante
     */
    private fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(
                isValid = false,
                errorMessage = "El nombre no puede estar vacío"
            )
            name.length < 2 -> ValidationResult(
                isValid = false,
                errorMessage = "El nombre debe tener al menos 2 caracteres"
            )
            name.any { it.isDigit() } -> ValidationResult(
                isValid = false,
                errorMessage = "El nombre no puede contener números"
            )
            else -> ValidationResult(isValid = true)
        }
    }

    /**
     * Valida grado académico
     */
    private fun validateGrade(grade: String): ValidationResult {
        return when {
            grade.isBlank() -> ValidationResult(
                isValid = false,
                errorMessage = "El grado no puede estar vacío"
            )
            !isValidGrade(grade) -> ValidationResult(
                isValid = false,
                errorMessage = "Grado no válido. Use: 1ero, 2do, 3ero, 4to, 5to"
            )
            else -> ValidationResult(isValid = true)
        }
    }

    /**
     * Verifica si el grado es válido
     */
    private fun isValidGrade(grade: String): Boolean {
        val validGrades = listOf("1ero", "2do", "3ero", "4to", "5to",
            "primero", "segundo", "tercero", "cuarto", "quinto")
        return validGrades.any { it.equals(grade, ignoreCase = true) }
    }
}