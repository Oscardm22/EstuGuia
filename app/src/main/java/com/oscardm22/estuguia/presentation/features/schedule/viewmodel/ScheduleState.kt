package com.oscardm22.estuguia.presentation.features.schedule.viewmodel

import com.oscardm22.estuguia.domain.models.Schedule

data class ScheduleState(
    val isLoading: Boolean = false,
    val schedules: List<Schedule> = emptyList(),
    val error: String? = null
)