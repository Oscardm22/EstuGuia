package com.oscardm22.estuguia.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val scheduleId: String = "",
    val dueDate: Date = Date(),
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = TaskStatus.PENDING,
    val reminderTime: Date? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) : Parcelable