package com.oscardm22.estuguia.presentation.features.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Modelos temporales
data class User(
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
class MainViewModel @Inject constructor() : ViewModel() {

    private val _userData = MutableStateFlow(User())
    val userData: StateFlow<User> = _userData.asStateFlow()

    private val _dashboardStats = MutableStateFlow(DashboardStats())
    val dashboardStats: StateFlow<DashboardStats> = _dashboardStats.asStateFlow()

    init {
        loadUserData()
        loadDashboardStats()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _userData.value = User(name = "Juan Pérez")
        }
    }

    private fun loadDashboardStats() {
        viewModelScope.launch {
            _dashboardStats.value = DashboardStats(
                todayClasses = 3,
                pendingTasks = 5,
                nextClassTime = "10:30"
            )
        }
    }
}