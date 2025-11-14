package com.oscardm22.estuguia.presentation.features.tasks.viewmodel

import com.oscardm22.estuguia.domain.models.Task

data class TaskState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTask: Task? = null,
    val filterStatus: String? = null,
    val filterSchedule: String? = null
)