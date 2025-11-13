package com.oscardm22.estuguia.domain.models

enum class DayOfWeek(val displayName: String, val index: Int) {
    MONDAY("Lunes", 1),
    TUESDAY("Martes", 2),
    WEDNESDAY("Miércoles", 3),
    THURSDAY("Jueves", 4),
    FRIDAY("Viernes", 5);

    companion object {
        fun fromIndex(index: Int): DayOfWeek {
            return DayOfWeek.entries.find { it.index == index } ?: MONDAY
        }

        fun getWeekdays(): List<DayOfWeek> {
            return listOf(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)
        }
    }
}