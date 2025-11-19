package com.oscardm22.estuguia.presentation.features.schedule.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.oscardm22.estuguia.domain.models.DayOfWeek
import com.oscardm22.estuguia.domain.models.Schedule
import com.oscardm22.estuguia.domain.usecases.schedule.AddScheduleUseCase
import com.oscardm22.estuguia.domain.usecases.schedule.DeleteScheduleUseCase
import com.oscardm22.estuguia.domain.usecases.schedule.GetSchedulesUseCase
import com.oscardm22.estuguia.domain.usecases.schedule.UpdateScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val getSchedulesUseCase: GetSchedulesUseCase,
    private val addScheduleUseCase: AddScheduleUseCase,
    private val deleteScheduleUseCase: DeleteScheduleUseCase,
    private val updateScheduleUseCase: UpdateScheduleUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _scheduleState = MutableStateFlow(ScheduleState())
    val scheduleState: StateFlow<ScheduleState> = _scheduleState.asStateFlow()

    private val _filteredSchedules = MutableStateFlow<List<Schedule>>(emptyList())
    val filteredSchedules: StateFlow<List<Schedule>> = _filteredSchedules.asStateFlow()

    private val _selectedDay = MutableStateFlow<String?>(null)

    fun getSchedules() {
        viewModelScope.launch {
            _scheduleState.value = _scheduleState.value.copy(isLoading = true, error = null)

            val userId = firebaseAuth.currentUser?.uid ?: ""
            if (userId.isEmpty()) {
                _scheduleState.value = _scheduleState.value.copy(
                    isLoading = false,
                    error = "Usuario no autenticado"
                )
                return@launch
            }

            val result = getSchedulesUseCase(userId)
            if (result.isSuccess) {
                val schedules = result.getOrDefault(emptyList())
                val sortedSchedules = sortSchedules(schedules)
                val stats = calculateStats(schedules)

                _scheduleState.value = _scheduleState.value.copy(
                    isLoading = false,
                    schedules = sortedSchedules,
                    stats = stats,
                    error = null
                )

                // APLICAR FILTRO ACTUAL DESPUÉS DE CARGAR - AGREGAR ESTA LINEA
                applyDayFilter(sortedSchedules, _selectedDay.value)
            } else {
                _scheduleState.value = _scheduleState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Error al cargar horarios"
                )
            }
        }
    }

    fun filterSchedulesByDay(day: String?) {
        _selectedDay.value = day
        applyDayFilter(_scheduleState.value.schedules, day)
    }

    private fun applyDayFilter(allSchedules: List<Schedule>, day: String?) {
        val filtered = if (day == null) {
            allSchedules
        } else {
            val dayEnum = DayOfWeek.fromDisplayName(day)
            allSchedules.filter { it.dayOfWeek == dayEnum.index }
        }
        _filteredSchedules.value = filtered
    }

    private fun calculateStats(schedules: List<Schedule>): ScheduleStats {
        val totalClasses = schedules.size
        val uniqueDays = schedules.map { it.dayOfWeek }.distinct().size
        val uniqueTurns = schedules.map { it.turn }.distinct().size

        return ScheduleStats(
            totalClasses = totalClasses,
            uniqueDays = uniqueDays,
            uniqueTurns = uniqueTurns
        )
    }

    private fun sortSchedules(schedules: List<Schedule>): List<Schedule> {
        return schedules.sortedWith(
            compareBy<Schedule> { schedule ->
                schedule.dayOfWeek
            }.thenBy { schedule ->
                schedule.startTime
            }.thenBy { schedule ->
                schedule.courseName
            }
        )
    }

    fun addSchedule(schedule: Schedule) {
        viewModelScope.launch {
            _scheduleState.value = _scheduleState.value.copy(isLoading = true, error = null)

            val userId = firebaseAuth.currentUser?.uid ?: ""
            if (userId.isEmpty()) {
                _scheduleState.value = _scheduleState.value.copy(
                    isLoading = false,
                    error = "Usuario no autenticado"
                )
                return@launch
            }

            val scheduleWithUser = schedule.copy(userId = userId)
            val result = addScheduleUseCase(scheduleWithUser)
            if (result.isSuccess) {
                _scheduleState.value = _scheduleState.value.copy(
                    isLoading = false,
                    error = null
                )
                getSchedules()
            } else {
                _scheduleState.value = _scheduleState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Error al agregar horario"
                )
            }
        }
    }

    fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            _scheduleState.value = _scheduleState.value.copy(isLoading = true, error = null)

            val userId = firebaseAuth.currentUser?.uid ?: ""
            if (userId.isEmpty()) {
                _scheduleState.value = _scheduleState.value.copy(
                    isLoading = false,
                    error = "Usuario no autenticado"
                )
                return@launch
            }

            val scheduleWithUser = schedule.copy(userId = userId)
            val result = updateScheduleUseCase(scheduleWithUser)
            if (result.isSuccess) {
                _scheduleState.value = _scheduleState.value.copy(
                    isLoading = false,
                    error = null
                )
                getSchedules()
            } else {
                _scheduleState.value = _scheduleState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Error al actualizar horario"
                )
            }
        }
    }

    fun deleteSchedule(scheduleId: String) {
        viewModelScope.launch {
            _scheduleState.value = _scheduleState.value.copy(isLoading = true, error = null)

            val result = deleteScheduleUseCase(scheduleId)
            if (result.isSuccess) {
                _scheduleState.value = _scheduleState.value.copy(
                    isLoading = false,
                    error = null
                )
                getSchedules()
            } else {
                _scheduleState.value = _scheduleState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Error al eliminar horario"
                )
            }
        }
    }
}