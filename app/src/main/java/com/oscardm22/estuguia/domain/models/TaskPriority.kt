package com.oscardm22.estuguia.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TaskPriority : Parcelable {
    LOW, MEDIUM, HIGH
}