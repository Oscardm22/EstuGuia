package com.oscardm22.estuguia.data.model

import java.util.Date

data class TaskDto(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val scheduleId: String = "",
    val dueDate: Date = Date(),
    val priority: String = "MEDIUM",
    val status: String = "PENDING",
    val reminderTime: Date? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val userId: String = ""
)