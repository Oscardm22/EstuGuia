package com.oscardm22.estuguia.presentation.features.schedule.ui.components

class ScheduleFormValidator {

    fun validateForm(
        courseName: String,
        startTime: String,
        endTime: String
    ): ValidationResult {
        return when {
            courseName.isEmpty() -> ValidationResult(
                isValid = false,
                errorMessage = "El nombre de la materia es requerido"
            )
            startTime.isEmpty() || endTime.isEmpty() -> ValidationResult(
                isValid = false,
                errorMessage = "Por favor selecciona el horario de inicio y fin"
            )
            startTime >= endTime -> ValidationResult(
                isValid = false,
                errorMessage = "La hora de inicio debe ser antes de la hora de fin"
            )
            else -> ValidationResult(isValid = true)
        }
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)