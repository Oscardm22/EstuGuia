package com.oscardm22.estuguia.domain.utils

/**
 * Representa el resultado de una validación de datos
 * @param isValid Indica si la validación fue exitosa
 * @param errorMessage Mensaje de error si la validación falló
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)