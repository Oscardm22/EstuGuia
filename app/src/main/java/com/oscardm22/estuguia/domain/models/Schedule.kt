package com.oscardm22.estuguia.domain.models

import android.os.Parcelable
import com.oscardm22.estuguia.data.model.ScheduleDto
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Schedule(
    val id: String = "",
    val userId: String = "",
    val courseName: String = "",
    val courseCode: String = "",
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
    val turn: Turn = Turn.MORNING,
    val classroom: String = "",
    val professor: String = "",
    val color: Int = 0,
    val createdAt: Date = Date()
) : Parcelable {
    fun toDto(): ScheduleDto = ScheduleDto(
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

@Parcelize
enum class Turn : Parcelable {
    MORNING, AFTERNOON
}