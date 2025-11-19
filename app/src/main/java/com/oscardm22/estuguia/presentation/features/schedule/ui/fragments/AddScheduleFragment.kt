package com.oscardm22.estuguia.presentation.features.schedule.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.oscardm22.estuguia.R
import com.oscardm22.estuguia.databinding.FragmentAddScheduleBinding
import com.oscardm22.estuguia.domain.models.Schedule
import com.oscardm22.estuguia.domain.utils.ScheduleUtils
import com.oscardm22.estuguia.presentation.features.schedule.ui.components.TimePickerManager
import com.oscardm22.estuguia.presentation.features.schedule.ui.components.ScheduleFormValidator
import com.oscardm22.estuguia.presentation.features.schedule.ui.components.TurnManager
import com.oscardm22.estuguia.presentation.features.schedule.ui.components.ColorManager
import com.oscardm22.estuguia.presentation.features.schedule.viewmodel.ScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddScheduleFragment : Fragment() {

    private var _binding: FragmentAddScheduleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ScheduleViewModel by viewModels()
    private val args: AddScheduleFragmentArgs by navArgs()

    private lateinit var timePickerManager: TimePickerManager
    private lateinit var formValidator: ScheduleFormValidator
    private lateinit var turnManager: TurnManager
    private lateinit var colorManager: ColorManager

    private var selectedStartTime: String = ""
    private var selectedEndTime: String = ""
    private var selectedDay: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeManagers()
        setupUI()
        setupClickListeners()

        // Si estamos editando, cargar los datos existentes
        args.schedule?.let { schedule ->
            populateForm(schedule)
        }
    }

    private fun initializeManagers() {
        timePickerManager = TimePickerManager(requireContext()) { time, isStartTime ->
            onTimeSelected(time, isStartTime)
        }
        formValidator = ScheduleFormValidator()
        turnManager = TurnManager()
        colorManager = ColorManager(requireContext())
    }

    private fun setupUI() {
        // Configurar spinner de días
        val daysAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            ScheduleUtils.daysOfWeek.map { it.first }
        )
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDay.adapter = daysAdapter

        // Cambiar textos por defecto para indicar selección
        binding.buttonStartTime.text = getString(R.string.select_start_time)
        binding.buttonEndTime.text = getString(R.string.select_end_time)

        // Inicializar como vacío hasta que se seleccionen
        selectedStartTime = ""
        selectedEndTime = ""

        binding.textTurnDisplay.visibility = View.GONE
    }

    private fun setupClickListeners() {
        binding.buttonStartTime.setOnClickListener {
            timePickerManager.showTimePicker(true)
        }

        binding.buttonEndTime.setOnClickListener {
            timePickerManager.showTimePicker(false)
        }

        binding.buttonSave.setOnClickListener {
            saveSchedule()
        }

        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun onTimeSelected(time: String, isStartTime: Boolean) {
        if (isStartTime) {
            selectedStartTime = time
            binding.buttonStartTime.text = time
        } else {
            selectedEndTime = time
            binding.buttonEndTime.text = time
        }
        updateTurnDisplay()
    }

    private fun populateForm(schedule: Schedule) {
        with(binding) {
            editTextCourseName.setText(schedule.courseName)
            editTextClassroom.setText(schedule.classroom)
            editTextProfessor.setText(schedule.professor)

            selectedStartTime = schedule.startTime
            selectedEndTime = schedule.endTime
            buttonStartTime.text = schedule.startTime
            buttonEndTime.text = schedule.endTime

            // Seleccionar día
            val dayPosition = ScheduleUtils.daysOfWeek.indexOfFirst { it.second == schedule.dayOfWeek }
            if (dayPosition != -1) {
                spinnerDay.setSelection(dayPosition)
            }

            updateTurnDisplay()
        }
    }

    private fun updateTurnDisplay() {
        // Validar que selectedStartTime no esté vacío antes de determinar el turno
        if (selectedStartTime.isEmpty()) {
            binding.textTurnDisplay.visibility = View.GONE
            return
        }

        val turn = turnManager.determineTurnFromTime(selectedStartTime)
        val turnName = turnManager.getTurnDisplayName(turn)
        binding.textTurnDisplay.text = getString(R.string.turn_display, turnName)
        binding.textTurnDisplay.visibility = View.VISIBLE
    }

    private fun saveSchedule() {
        val courseName = binding.editTextCourseName.text.toString().trim()
        val classroom = binding.editTextClassroom.text.toString().trim()
        val professor = binding.editTextProfessor.text.toString().trim()

        // USAR FORM VALIDATOR
        val validation = formValidator.validateForm(courseName, selectedStartTime, selectedEndTime)
        if (!validation.isValid) {
            showError(validation.errorMessage)
            return
        }

        val selectedDayPosition = binding.spinnerDay.selectedItemPosition
        selectedDay = ScheduleUtils.daysOfWeek[selectedDayPosition].second

        val turn = turnManager.determineTurnFromTime(selectedStartTime)
        val color = colorManager.getColorForCourse(courseName)

        val newSchedule = Schedule(
            id = args.schedule?.id ?: "",
            courseName = courseName,
            dayOfWeek = selectedDay,
            startTime = selectedStartTime,
            endTime = selectedEndTime,
            turn = turn,
            classroom = classroom,
            professor = professor,
            color = color
        )

        if (args.schedule != null) {
            viewModel.updateSchedule(newSchedule)
        } else {
            viewModel.addSchedule(newSchedule)
        }

        Toast.makeText(requireContext(), "Horario guardado", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    private fun showError(message: String?) {
        when {
            message?.contains("materia") == true -> {
                binding.textInputCourseName.error = message
            }
            else -> {
                Toast.makeText(requireContext(), message ?: "Error desconocido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}