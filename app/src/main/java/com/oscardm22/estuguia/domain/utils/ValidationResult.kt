package com.oscardm22.estuguia.domain.utils

/**
 * Representa el resultado de una validación de datos
 * @param isValid Indica si la validación fue exitosa
 * @param errorMessage Mensaje de error si la validación falló
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
) {
    companion object {
        /**
         * Crea un ValidationResult válido
         */
        fun valid(): ValidationResult = ValidationResult(isValid = true)

        /**
         * Crea un ValidationResult inválido con mensaje de error
         */
        fun invalid(errorMessage: String): ValidationResult =
            ValidationResult(isValid = false, errorMessage = errorMessage)
    }
}