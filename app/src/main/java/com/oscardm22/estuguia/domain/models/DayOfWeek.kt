package com.oscardm22.estuguia.domain.models

enum class DayOfWeek(val displayName: String, val index: Int) {
    MONDAY("Lunes", 1),
    TUESDAY("Martes", 2),
    WEDNESDAY("Miércoles", 3),
    THURSDAY("Jueves", 4),
    FRIDAY("Viernes", 5);

    companion object {
        fun fromDisplayName(displayName: String): DayOfWeek {
            return entries.find { it.displayName == displayName } ?: MONDAY
        }
    }
}