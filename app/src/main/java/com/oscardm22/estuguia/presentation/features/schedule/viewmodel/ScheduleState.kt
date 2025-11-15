package com.oscardm22.estuguia.presentation.features.schedule.viewmodel

import com.oscardm22.estuguia.domain.models.Schedule

data class ScheduleState(
    val isLoading: Boolean = false,
    val schedules: List<Schedule> = emptyList(),
    val error: String? = null,
    val stats: ScheduleStats = ScheduleStats()
)

data class ScheduleStats(
    val totalClasses: Int = 0,
    val uniqueDays: Int = 0,
    val uniqueTurns: Int = 0
)