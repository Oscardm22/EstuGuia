package com.oscardm22.estuguia.presentation.features.tasks.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.oscardm22.estuguia.databinding.FragmentAddTaskBinding
import com.oscardm22.estuguia.domain.models.Task
import com.oscardm22.estuguia.domain.models.TaskPriority
import com.oscardm22.estuguia.domain.models.TaskStatus
import com.oscardm22.estuguia.domain.repositories.AuthRepository
import com.oscardm22.estuguia.domain.utils.NotificationScheduler
import com.oscardm22.estuguia.presentation.features.tasks.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddTaskFragment : Fragment() {

    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by activityViewModels()

    private val args: AddTaskFragmentArgs by navArgs()

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

        args.task?.let { task ->
            populateForm(task)
        }

        setupClickListeners()
        setupObservers()
        setupPrioritySelector()
        setupReminderSelector()
        loadSchedules()
    }

    private fun populateForm(task: Task) {
        with(binding) {
            // Cargar datos básicos
            editTextTitle.setText(task.title)
            editTextDescription.setText(task.description)

            // Cargar fecha de vencimiento
            selectedDueDate.time = task.dueDate
            updateDueDateButton()

            // Cargar prioridad
            selectedPriority = task.priority
            when (task.priority) {
                TaskPriority.LOW -> onPrioritySelected(TaskPriority.LOW, chipLow)
                TaskPriority.MEDIUM -> onPrioritySelected(TaskPriority.MEDIUM, chipMedium)
                TaskPriority.HIGH -> onPrioritySelected(TaskPriority.HIGH, chipHigh)
            }

            // Cargar recordatorio si existe
            task.reminderTime?.let { reminderTime ->
                val reminderCalendar = Calendar.getInstance().apply { time = reminderTime }
                val dueCalendar = Calendar.getInstance().apply { time = task.dueDate }

                val daysDifference = ((dueCalendar.timeInMillis - reminderCalendar.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()

                when (daysDifference) {
                    1 -> onReminderSelected(ReminderType.ONE_DAY_BEFORE, chipReminder1Day)
                    2 -> onReminderSelected(ReminderType.TWO_DAYS_BEFORE, chipReminder2Days)
                    3 -> onReminderSelected(ReminderType.THREE_DAYS_BEFORE, chipReminder3Days)
                    else -> {
                        customReminderTime = reminderCalendar
                        onReminderSelected(ReminderType.CUSTOM, chipCustomReminder)
                        updateCustomReminderChip()
                    }
                }
            }

            // Cambiar título y texto del botón si estamos editando
            binding.textViewTitle.text = "Editar Tarea"
            binding.btnSaveTask.text = "Actualizar Tarea"
        }
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.schedules.collect { schedulesList ->
                schedules.clear()
                scheduleNames.clear()

                schedules.addAll(schedulesList)
                scheduleNames.add("Sin materia específica")
                schedulesList.forEach { schedule ->
                    val scheduleName = "${schedule.courseName} - ${getDayName(schedule.dayOfWeek)}"
                    scheduleNames.add(scheduleName)
                }
                setupScheduleSpinner()

                args.task?.let { task ->
                    if (task.scheduleId.isNotEmpty()) {
                        val scheduleIndex = schedules.indexOfFirst { it.id == task.scheduleId }
                        if (scheduleIndex != -1) {
                            binding.spinnerSchedule.setSelection(scheduleIndex + 1)
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                if (state.error != null) {
                    // Mostrar error al usuario
                    android.widget.Toast.makeText(
                        requireContext(),
                        state.error,
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupScheduleSpinner() {
        if (scheduleNames.isEmpty()) {
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

        binding.spinnerSchedule.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    selectedScheduleId = ""
                } else {
                    val scheduleIndex = position - 1
                    if (scheduleIndex < schedules.size) {
                        selectedScheduleId = schedules[scheduleIndex].id
                    }
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                selectedScheduleId = ""
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
        viewLifecycleOwner.lifecycleScope.launch {
            val userId = getCurrentUserId()
            if (userId != null) {
                viewModel.loadSchedules(userId)
            } else {
                handleUserNotAuthenticated()
            }
        }
    }

    private suspend fun getCurrentUserId(): String? {
        return try {
            authRepository.getCurrentUserId()
        } catch (e: Exception) {
            null
        }
    }

    private fun handleUserNotAuthenticated() {
        android.widget.Toast.makeText(
            requireContext(),
            "Debes iniciar sesión para agregar tareas",
            android.widget.Toast.LENGTH_LONG
        ).show()

        binding.root.postDelayed({
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }, 2000)
    }

    private fun setupPrioritySelector() {
        binding.chipLow.setOnClickListener { onPrioritySelected(TaskPriority.LOW, binding.chipLow) }
        binding.chipMedium.setOnClickListener { onPrioritySelected(TaskPriority.MEDIUM, binding.chipMedium) }
        binding.chipHigh.setOnClickListener { onPrioritySelected(TaskPriority.HIGH, binding.chipHigh) }

        // Solo establecer MEDIUM por defecto si no estamos editando
        if (args.task == null) {
            onPrioritySelected(TaskPriority.MEDIUM, binding.chipMedium)
        }
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

        // Solo establecer NONE por defecto si no estamos editando
        if (args.task == null) {
            onReminderSelected(ReminderType.NONE, binding.chipNoReminder)
        }
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
        viewLifecycleOwner.lifecycleScope.launch {
            val userId = getCurrentUserId()
            if (userId == null) {
                handleUserNotAuthenticated()
                return@launch
            }

            val task = if (args.task != null) {
                args.task!!.reminderTime?.let { oldReminderTime ->
                    NotificationScheduler.cancelTaskReminder(
                        requireContext(),
                        args.task!!.id
                    )
                }

                args.task!!.copy(
                    title = binding.editTextTitle.text.toString(),
                    description = binding.editTextDescription.text.toString(),
                    scheduleId = selectedScheduleId,
                    dueDate = selectedDueDate.time,
                    priority = selectedPriority,
                    reminderTime = calculateReminderTime(),
                    updatedAt = Date()
                )
            } else {
                Task(
                    id = UUID.randomUUID().toString(),
                    title = binding.editTextTitle.text.toString(),
                    description = binding.editTextDescription.text.toString(),
                    scheduleId = selectedScheduleId,
                    dueDate = selectedDueDate.time,
                    priority = selectedPriority,
                    status = TaskStatus.PENDING,
                    reminderTime = calculateReminderTime()
                )
            }

            // PROGRAMAR NOTIFICACIÓN si hay recordatorio
            task.reminderTime?.let { reminderTime ->
                val message = when (selectedReminderType) {
                    ReminderType.ONE_DAY_BEFORE -> "Tu tarea '${task.title}' vence mañana"
                    ReminderType.TWO_DAYS_BEFORE -> "Tu tarea '${task.title}' vence en 2 días"
                    ReminderType.THREE_DAYS_BEFORE -> "Tu tarea '${task.title}' vence en 3 días"
                    ReminderType.CUSTOM -> "Recordatorio: '${task.title}'"
                    else -> "No olvides completar '${task.title}'"
                }

                NotificationScheduler.scheduleTaskReminder(
                    context = requireContext(),
                    taskId = task.id,
                    taskTitle = task.title,
                    reminderTime = reminderTime,
                    message = message
                )
            }

            // Guardar en ViewModel
            if (args.task != null) {
                viewModel.updateTask(task, userId)
            } else {
                viewModel.addTask(task, userId)
            }

            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}