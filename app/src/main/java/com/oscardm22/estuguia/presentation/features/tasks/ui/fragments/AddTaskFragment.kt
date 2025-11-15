package com.oscardm22.estuguia.presentation.features.tasks.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.oscardm22.estuguia.databinding.FragmentAddTaskBinding
import com.oscardm22.estuguia.domain.models.Task
import com.oscardm22.estuguia.domain.models.TaskPriority
import com.oscardm22.estuguia.domain.models.TaskStatus
import com.oscardm22.estuguia.domain.repositories.AuthRepository
import com.oscardm22.estuguia.presentation.features.tasks.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class AddTaskFragment : Fragment() {

    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by viewModels()

    @Inject
    lateinit var authRepository: AuthRepository

    private var selectedDueDate: Calendar = Calendar.getInstance()
    private var selectedPriority: TaskPriority = TaskPriority.MEDIUM
    private var selectedReminderType: ReminderType = ReminderType.NONE
    private var customReminderTime: Calendar? = null
    private var selectedScheduleId: String = ""
    private val schedules = mutableListOf<com.oscardm22.estuguia.domain.models.Schedule>()
    private val scheduleNames = mutableListOf<String>()

    enum class ReminderType {
        NONE, ONE_DAY_BEFORE, TWO_DAYS_BEFORE, THREE_DAYS_BEFORE, CUSTOM
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("DEBUG", "AddTaskFragment - onViewCreated: Iniciando fragment")

        setupClickListeners()
        setupObservers()
        setupPrioritySelector()
        setupReminderSelector()
        loadSchedules() // Cargar las materias/horarios
    }

    private fun setupClickListeners() {
        binding.btnSaveTask.setOnClickListener {
            if (validateForm()) {
                saveTask()
            }
        }

        binding.btnCancel.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.buttonDueDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun setupObservers() {
        Log.d("DEBUG", "AddTaskFragment - setupObservers: Configurando observadores")

        // Observar los horarios del ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            Log.d("DEBUG", "AddTaskFragment - Observer: Iniciando collector de schedules")

            viewModel.schedules.collect { schedulesList ->
                Log.d("DEBUG", "AddTaskFragment - Observer: Received schedules list with ${schedulesList.size} items")

                if (schedulesList.isNotEmpty()) {
                    Log.d("DEBUG", "AddTaskFragment - Observer: First schedule: ${schedulesList.first().courseName}")
                } else {
                    Log.d("DEBUG", "AddTaskFragment - Observer: schedulesList is EMPTY")
                }

                schedules.clear()
                scheduleNames.clear()

                schedules.addAll(schedulesList)
                Log.d("DEBUG", "AddTaskFragment - Observer: schedules list now has ${schedules.size} items")

                // Agregar opción "Sin materia específica"
                scheduleNames.add("Sin materia específica")
                Log.d("DEBUG", "AddTaskFragment - Observer: Added default option")

                // Agregar las materias del usuario
                schedulesList.forEach { schedule ->
                    val scheduleName = "${schedule.courseName} - ${getDayName(schedule.dayOfWeek)}"
                    scheduleNames.add(scheduleName)
                    Log.d("DEBUG", "AddTaskFragment - Observer: Added schedule: $scheduleName")
                }

                Log.d("DEBUG", "AddTaskFragment - Observer: scheduleNames now has ${scheduleNames.size} items")
                setupScheduleSpinner()
            }
        }

        // También observar el estado para posibles errores
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                if (state.error != null) {
                    Log.e("DEBUG", "AddTaskFragment - Error in state: ${state.error}")
                }
                if (state.isLoading) {
                    Log.d("DEBUG", "AddTaskFragment - Loading state: true")
                }
            }
        }
    }

    private fun setupScheduleSpinner() {
        Log.d("DEBUG", "AddTaskFragment - setupScheduleSpinner: Configurando spinner con ${scheduleNames.size} items")

        if (scheduleNames.isEmpty()) {
            Log.w("DEBUG", "AddTaskFragment - setupScheduleSpinner: scheduleNames está vacío, usando datos de prueba")
            // Datos de fallback
            scheduleNames.add("Sin materia específica")
            scheduleNames.add("Matemáticas - Lunes")
            scheduleNames.add("Programación - Miércoles")
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            scheduleNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSchedule.adapter = adapter

        Log.d("DEBUG", "AddTaskFragment - setupScheduleSpinner: Adapter configurado con ${adapter.count} items")
        Log.d("DEBUG", "AddTaskFragment - setupScheduleSpinner: Spinner tiene ${binding.spinnerSchedule.count} items")

        // Listener para cuando se selecciona una materia
        binding.spinnerSchedule.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.d("DEBUG", "AddTaskFragment - Spinner item selected: position=$position")
                if (position == 0) {
                    selectedScheduleId = ""
                    Log.d("DEBUG", "AddTaskFragment - No specific subject selected")
                } else {
                    val scheduleIndex = position - 1
                    if (scheduleIndex < schedules.size) {
                        selectedScheduleId = schedules[scheduleIndex].id
                        Log.d("DEBUG", "AddTaskFragment - Selected schedule ID: $selectedScheduleId, Name: ${schedules[scheduleIndex].courseName}")
                    } else {
                        Log.w("DEBUG", "AddTaskFragment - scheduleIndex out of bounds: $scheduleIndex, schedules size: ${schedules.size}")
                    }
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                selectedScheduleId = ""
                Log.d("DEBUG", "AddTaskFragment - Nothing selected in spinner")
            }
        }
    }

    private fun getDayName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            1 -> "Lunes"
            2 -> "Martes"
            3 -> "Miércoles"
            4 -> "Jueves"
            5 -> "Viernes"
            6 -> "Sábado"
            7 -> "Domingo"
            else -> "Desconocido"
        }
    }

    private fun loadSchedules() {
        val userId = getCurrentUserId()
        Log.d("DEBUG", "AddTaskFragment - loadSchedules: Llamando viewModel.loadSchedules con userId: $userId")
        viewModel.loadSchedules(userId)
    }

    private fun getCurrentUserId(): String {
        return try {
            // Usar runBlocking para llamar a la función suspend
            runBlocking {
                authRepository.getCurrentUserId().also { userId ->
                    if (userId != null) {
                        Log.d("DEBUG", "AddTaskFragment - getCurrentUserId: ✅ Usuario autenticado, ID: $userId")
                    } else {
                        Log.e("DEBUG", "AddTaskFragment - getCurrentUserId: ❌ No hay usuario autenticado")
                    }
                }
            } ?: run {
                handleUserNotAuthenticated()
                "no_user_authenticated"
            }
        } catch (e: Exception) {
            Log.e("DEBUG", "AddTaskFragment - getCurrentUserId: 💥 Error: ${e.message}")
            handleUserNotAuthenticated()
            "error_getting_user"
        }
    }

    private fun handleUserNotAuthenticated() {
        // Mostrar mensaje al usuario
        android.widget.Toast.makeText(
            requireContext(),
            "Debes iniciar sesión para agregar tareas",
            android.widget.Toast.LENGTH_LONG
        ).show()

        // Opcional: redirigir al login después de un tiempo
        binding.root.postDelayed({
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }, 2000)
    }

    // ... (el resto de los métodos se mantienen igual - setupPrioritySelector, showDatePicker, saveTask, etc.) ...

    private fun setupPrioritySelector() {
        binding.chipLow.setOnClickListener { onPrioritySelected(TaskPriority.LOW, binding.chipLow) }
        binding.chipMedium.setOnClickListener { onPrioritySelected(TaskPriority.MEDIUM, binding.chipMedium) }
        binding.chipHigh.setOnClickListener { onPrioritySelected(TaskPriority.HIGH, binding.chipHigh) }
        onPrioritySelected(TaskPriority.MEDIUM, binding.chipMedium)
    }

    private fun setupReminderSelector() {
        binding.chipNoReminder.setOnClickListener { onReminderSelected(ReminderType.NONE, binding.chipNoReminder) }
        binding.chipReminder1Day.setOnClickListener { onReminderSelected(ReminderType.ONE_DAY_BEFORE, binding.chipReminder1Day) }
        binding.chipReminder2Days.setOnClickListener { onReminderSelected(ReminderType.TWO_DAYS_BEFORE, binding.chipReminder2Days) }
        binding.chipReminder3Days.setOnClickListener { onReminderSelected(ReminderType.THREE_DAYS_BEFORE, binding.chipReminder3Days) }
        binding.chipCustomReminder.setOnClickListener {
            onReminderSelected(ReminderType.CUSTOM, binding.chipCustomReminder)
            showCustomReminderPicker()
        }
        onReminderSelected(ReminderType.NONE, binding.chipNoReminder)
    }

    private fun onPrioritySelected(priority: TaskPriority, selectedChip: Chip) {
        selectedPriority = priority
        binding.chipLow.isChecked = false
        binding.chipMedium.isChecked = false
        binding.chipHigh.isChecked = false
        selectedChip.isChecked = true
    }

    private fun onReminderSelected(reminderType: ReminderType, selectedChip: Chip) {
        selectedReminderType = reminderType
        binding.chipNoReminder.isChecked = false
        binding.chipReminder1Day.isChecked = false
        binding.chipReminder2Days.isChecked = false
        binding.chipReminder3Days.isChecked = false
        binding.chipCustomReminder.isChecked = false
        selectedChip.isChecked = true
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedDueDate.set(selectedYear, selectedMonth, selectedDay)
                updateDueDateButton()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun showCustomReminderPicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                customReminderTime = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay, 8, 0)
                }
                showCustomReminderTimePicker()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.setTitle("Seleccionar fecha del recordatorio")
        datePickerDialog.show()
    }

    private fun showCustomReminderTimePicker() {
        val calendar = customReminderTime ?: Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                customReminderTime?.set(Calendar.HOUR_OF_DAY, selectedHour)
                customReminderTime?.set(Calendar.MINUTE, selectedMinute)
                updateCustomReminderChip()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.setTitle("Seleccionar hora del recordatorio")
        timePickerDialog.show()
    }

    private fun updateDueDateButton() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.buttonDueDate.text = "Vence: ${dateFormat.format(selectedDueDate.time)}"
    }

    private fun updateCustomReminderChip() {
        customReminderTime?.let {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            binding.chipCustomReminder.text = "Personalizado: ${dateFormat.format(it.time)}"
        }
    }

    private fun calculateReminderTime(): Date? {
        return when (selectedReminderType) {
            ReminderType.NONE -> null
            ReminderType.ONE_DAY_BEFORE -> {
                val reminder = selectedDueDate.clone() as Calendar
                reminder.add(Calendar.DAY_OF_MONTH, -1)
                reminder.time
            }
            ReminderType.TWO_DAYS_BEFORE -> {
                val reminder = selectedDueDate.clone() as Calendar
                reminder.add(Calendar.DAY_OF_MONTH, -2)
                reminder.time
            }
            ReminderType.THREE_DAYS_BEFORE -> {
                val reminder = selectedDueDate.clone() as Calendar
                reminder.add(Calendar.DAY_OF_MONTH, -3)
                reminder.time
            }
            ReminderType.CUSTOM -> customReminderTime?.time
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true
        if (binding.editTextTitle.text.isNullOrEmpty()) {
            binding.textInputTitle.error = "El título es requerido"
            isValid = false
        } else {
            binding.textInputTitle.error = null
        }
        return isValid
    }

    private fun saveTask() {
        val title = binding.editTextTitle.text.toString()
        val description = binding.editTextDescription.text.toString()
        val dueDate = selectedDueDate.time
        val priority = selectedPriority
        val reminderTime = calculateReminderTime()

        Log.d("DEBUG", "AddTaskFragment - saveTask: Guardando tarea con scheduleId: $selectedScheduleId")

        val newTask = Task(
            title = title,
            description = description,
            scheduleId = selectedScheduleId,
            dueDate = dueDate,
            priority = priority,
            status = TaskStatus.PENDING,
            reminderTime = reminderTime
        )

        val userId = getCurrentUserId()
        viewModel.addTask(newTask, userId)
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}