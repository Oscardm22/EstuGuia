package com.oscardm22.estuguia.presentation.features.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oscardm22.estuguia.domain.models.Schedule
import com.oscardm22.estuguia.domain.usecases.auth.GetCurrentUserProfileUseCase
import com.oscardm22.estuguia.domain.usecases.dashboard.GetTodaySchedulesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class DashboardUser(
    val name: String = "Estudiante",
    val email: String = "",
    val grade: String = "",
    val section: String = ""
)

data class DashboardStats(
    val todayClasses: Int = 0,
    val pendingTasks: Int = 0,
    val nextClassTime: String? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentUserProfileUseCase: GetCurrentUserProfileUseCase,
    private val getTodaySchedulesUseCase: GetTodaySchedulesUseCase
) : ViewModel() {

    private val _userData = MutableStateFlow(DashboardUser())
    val userData: StateFlow<DashboardUser> = _userData.asStateFlow()

    private val _dashboardStats = MutableStateFlow(DashboardStats())
    val dashboardStats: StateFlow<DashboardStats> = _dashboardStats.asStateFlow()

    init {
        loadUserData()
        loadDashboardStats()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            try {
                val result = getCurrentUserProfileUseCase()
                if (result.isSuccess) {
                    val user = result.getOrThrow()
                    _userData.value = DashboardUser(
                        name = user.getDisplayName(),
                        email = user.email,
                        grade = user.grade,
                        section = user.section ?: ""
                    )
                }
            } catch (e: Exception) {
                // Fallback a datos básicos si hay error
                _userData.value = DashboardUser(name = "Estudiante")
            }
        }
    }

    private fun loadDashboardStats() {
        viewModelScope.launch {
            try {
                val todaySchedules = getTodaySchedulesUseCase()
                val nextClass = calculateNextClass(todaySchedules)

                _dashboardStats.value = DashboardStats(
                    todayClasses = todaySchedules.size,
                    pendingTasks = 0,
                    nextClassTime = nextClass
                )
            } catch (e: Exception) {
                _dashboardStats.value = DashboardStats(
                    todayClasses = 0,
                    pendingTasks = 0,
                    nextClassTime = null
                )
            }
        }
    }

    private fun calculateNextClass(schedules: List<Schedule>): String? {
        if (schedules.isEmpty()) return null

        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        return schedules
            .filter { it.startTime > currentTime }
            .minByOrNull { it.startTime }
            ?.startTime
    }

    // Función para refrescar datos
    fun refreshData() {
        loadUserData()
        loadDashboardStats()
    }
}