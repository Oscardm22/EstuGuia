package com.oscardm22.estuguia.presentation.features.schedule.ui.components

import com.oscardm22.estuguia.domain.models.Turn

class TurnManager {

    fun determineTurnFromTime(startTime: String): Turn {
        if (startTime.isEmpty()) return Turn.MORNING

        return try {
            val hour = startTime.split(":")[0].toInt()
            when {
                hour in 0..11 -> Turn.MORNING
                else -> Turn.AFTERNOON
            }
        } catch (e: Exception) {
            Turn.MORNING
        }
    }

    fun getTurnDisplayName(turn: Turn): String {
        return when (turn) {
            Turn.MORNING -> "Mañana"
            Turn.AFTERNOON -> "Tarde"
        }
    }
}