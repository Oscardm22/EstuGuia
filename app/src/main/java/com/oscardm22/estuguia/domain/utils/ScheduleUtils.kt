package com.oscardm22.estuguia.domain.utils

import com.oscardm22.estuguia.domain.models.Schedule
import com.oscardm22.estuguia.domain.models.Turn

object ScheduleUtils {

    val daysOfWeek = listOf(
        "Lunes" to 1,
        "Martes" to 2,
        "Miércoles" to 3,
        "Jueves" to 4,
        "Viernes" to 5
    )

    val turns = listOf(
        "Mañana" to Turn.MORNING,
        "Tarde" to Turn.AFTERNOON
    )

    fun getDayName(dayOfWeek: Int): String {
        return daysOfWeek.find { it.second == dayOfWeek }?.first ?: "Desconocido"
    }

    fun getTurnName(turn: Turn): String {
        return turns.find { it.second == turn }?.first ?: "Desconocido"
    }
}