package com.oscardm22.estuguia.data.model

import com.oscardm22.estuguia.domain.models.Schedule
import com.oscardm22.estuguia.domain.models.Turn
import java.util.Date

data class ScheduleDto(
    val id: String = "",
    val userId: String = "",
    val courseName: String = "",
    val courseCode: String = "",
    val dayOfWeek: Int = 1,
    val startTime: String = "08:00",
    val endTime: String = "09:00",
    val turn: Turn = Turn.MORNING,
    val classroom: String = "",
    val professor: String = "",
    val color: Int = 0,
    val createdAt: Date = Date()
) {
    fun toDomain(): Schedule = Schedule(
        id = id,
        userId = userId,
        courseName = courseName,
        courseCode = courseCode,
        dayOfWeek = dayOfWeek,
        startTime = startTime,
        endTime = endTime,
        turn = turn,
        classroom = classroom,
        professor = professor,
        color = color,
        createdAt = createdAt
    )
}