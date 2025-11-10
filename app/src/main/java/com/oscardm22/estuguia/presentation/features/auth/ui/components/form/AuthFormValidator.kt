package com.oscardm22.estuguia.presentation.features.auth.ui.components.form

import android.util.Patterns
import com.google.android.material.textfield.TextInputLayout

/**
 * Maneja toda la validación de formularios de autenticación
 * Centraliza la lógica de validación para Login y Register
 */
class AuthFormValidator {

    /**
     * Resultado de una validación individual
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    /**
     * Valida el formulario completo de Login
     * @return true si el formulario es válido
     */
    fun validateLoginForm(
        email: String,
        password: String,
        emailLayout: TextInputLayout? = null,
        passwordLayout: TextInputLayout? = null
    ): Boolean {
        var isValid = true

        // Validar email
        val emailValidation = validateEmail(email)
        if (!emailValidation.isValid) {
            emailLayout?.error = emailValidation.errorMessage
            isValid = false
        } else {
            emailLayout?.error = null
        }

        // Validar contraseña
        val passwordValidation = validatePassword(password)
        if (!passwordValidation.isValid) {
            passwordLayout?.error = passwordValidation.errorMessage
            isValid = false
        } else {
            passwordLayout?.error = null
        }

        return isValid
    }

    /**
     * Valida el formulario completo de Registro
     * @return true si el formulario es válido
     */
    fun validateRegisterForm(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        grade: String,
        nameLayout: TextInputLayout? = null,
        emailLayout: TextInputLayout? = null,
        passwordLayout: TextInputLayout? = null,
        confirmPasswordLayout: TextInputLayout? = null,
        gradeLayout: TextInputLayout? = null
    ): Boolean {
        var isValid = true

        // Validar nombre
        val nameValidation = validateName(name)
        if (!nameValidation.isValid) {
            nameLayout?.error = nameValidation.errorMessage
            isValid = false
        } else {
            nameLayout?.error = null
        }

        // Validar email
        val emailValidation = validateEmail(email)
        if (!emailValidation.isValid) {
            emailLayout?.error = emailValidation.errorMessage
            isValid = false
        } else {
            emailLayout?.error = null
        }

        // Validar contraseña
        val passwordValidation = validatePassword(password)
        if (!passwordValidation.isValid) {
            passwordLayout?.error = passwordValidation.errorMessage
            isValid = false
        } else {
            passwordLayout?.error = null
        }

        // Validar confirmación de contraseña
        val confirmPasswordValidation = validateConfirmPassword(password, confirmPassword)
        if (!confirmPasswordValidation.isValid) {
            confirmPasswordLayout?.error = confirmPasswordValidation.errorMessage
            isValid = false
        } else {
            confirmPasswordLayout?.error = null
        }

        // Validar grado
        val gradeValidation = validateGrade(grade)
        if (!gradeValidation.isValid) {
            gradeLayout?.error = gradeValidation.errorMessage
            isValid = false
        } else {
            gradeLayout?.error = null
        }

        return isValid
    }

    /**
     * Valida un email individual
     */
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isEmpty() -> ValidationResult(
                isValid = false,
                errorMessage = "El email es requerido"
            )
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> ValidationResult(
                isValid = false,
                errorMessage = "Ingresa un email válido"
            )
            else -> ValidationResult(isValid = true)
        }
    }

    /**
     * Valida una contraseña individual
     */
    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isEmpty() -> ValidationResult(
                isValid = false,
                errorMessage = "La contraseña es requerida"
            )
            password.length < 6 -> ValidationResult(
                isValid = false,
                errorMessage = "La contraseña debe tener al menos 6 caracteres"
            )
            else -> ValidationResult(isValid = true)
        }
    }

    /**
     * Valida un nombre individual
     */
    fun validateName(name: String): ValidationResult {
        return when {
            name.isEmpty() -> ValidationResult(
                isValid = false,
                errorMessage = "El nombre es requerido"
            )
            name.length < 2 -> ValidationResult(
                isValid = false,
                errorMessage = "El nombre debe tener al menos 2 caracteres"
            )
            else -> ValidationResult(isValid = true)
        }
    }

    /**
     * Valida la confirmación de contraseña
     */
    fun validateConfirmPassword(password: String, confirmPassword: String): ValidationResult {
        return when {
            confirmPassword.isEmpty() -> ValidationResult(
                isValid = false,
                errorMessage = "Confirma tu contraseña"
            )
            password != confirmPassword -> ValidationResult(
                isValid = false,
                errorMessage = "Las contraseñas no coinciden"
            )
            else -> ValidationResult(isValid = true)
        }
    }

    /**
     * Valida un grado académico
     */
    fun validateGrade(grade: String): ValidationResult {
        return when {
            grade.isEmpty() -> ValidationResult(
                isValid = false,
                errorMessage = "Selecciona tu grado académico"
            )
            !isValidGrade(grade) -> ValidationResult(
                isValid = false,
                errorMessage = "Grado no válido. Selecciona una opción de la lista"
            )
            else -> ValidationResult(isValid = true)
        }
    }

    /**
     * Verifica si el grado es válido
     */
    private fun isValidGrade(grade: String): Boolean {
        val validGrades = listOf("1ero", "2do", "3ero", "4to", "5to")
        return validGrades.any { it.equals(grade, ignoreCase = true) }
    }
}