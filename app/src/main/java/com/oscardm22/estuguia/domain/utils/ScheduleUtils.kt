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

    fun getTurnFromTime(startTime: String): Turn {
        val hour = startTime.split(":")[0].toInt()
        return if (hour < 12) Turn.MORNING else Turn.AFTERNOON
    }

    fun formatTimeForGrid(time: String): String {
        return try {
            if (time.startsWith("0")) time.substring(1) else time
        } catch (e: Exception) {
            time
        }
    }

    // Horarios típicos por turno
    val morningTimeSlots = listOf(
        "07:00", "08:00", "09:00", "10:00", "11:00", "12:00"
    )

    val afternoonTimeSlots = listOf(
        "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00"
    )

    private fun findScheduleAtTime(schedules: List<Schedule>, day: Int, time: String): Schedule? {
        fun normalize(t: String): String =
            t.trim()
                .replace(" ", "")
                .replace("a.m.", "", ignoreCase = true)
                .replace("p.m.", "", ignoreCase = true)
                .padStart(5, '0') // Asegura formato "07:00"

        val normalizedTime = normalize(time)
        return schedules.find {
            it.dayOfWeek == day && normalize(it.startTime) == normalizedTime
        }
    }


    // Modelo para las celdas del grid
    sealed class ScheduleGridCell {
        data class TimeCell(val time: String) : ScheduleGridCell()
        data class ScheduleCell(val schedule: Schedule) : ScheduleGridCell()
        data class EmptyCell(val day: Int, val time: String) : ScheduleGridCell()
    }
}