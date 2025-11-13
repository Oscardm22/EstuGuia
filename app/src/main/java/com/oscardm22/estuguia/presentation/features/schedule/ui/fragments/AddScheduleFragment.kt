package com.oscardm22.estuguia.presentation.features.schedule.ui.fragments

import android.app.TimePickerDialog
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
import com.oscardm22.estuguia.domain.models.Turn
import com.oscardm22.estuguia.domain.utils.ScheduleUtils
import com.oscardm22.estuguia.presentation.features.schedule.viewmodel.ScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddScheduleFragment : Fragment() {

    private var _binding: FragmentAddScheduleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ScheduleViewModel by viewModels()
    private val args: AddScheduleFragmentArgs by navArgs()

    private var selectedStartTime: String = "08:00"
    private var selectedEndTime: String = "10:00"
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

        setupUI()
        setupClickListeners()

        // Si estamos editando, cargar los datos existentes
        args.schedule?.let { schedule ->
            populateForm(schedule)
        }
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
    }

    private fun setupClickListeners() {
        binding.buttonStartTime.setOnClickListener {
            showTimePicker(true)
        }

        binding.buttonEndTime.setOnClickListener {
            showTimePicker(false)
        }

        binding.buttonSave.setOnClickListener {
            saveSchedule()
        }

        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun populateForm(schedule: Schedule) {
        with(binding) {
            editTextCourseName.setText(schedule.courseName)
            editTextCourseCode.setText(schedule.courseCode)
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

    private fun showTimePicker(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val time = String.format(Locale.US, "%02d:%02d", selectedHour, selectedMinute)

                if (isStartTime) {
                    selectedStartTime = time
                    binding.buttonStartTime.text = time
                } else {
                    selectedEndTime = time
                    binding.buttonEndTime.text = time
                }

                updateTurnDisplay()
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun updateTurnDisplay() {
        val turn = determineTurnFromTime(selectedStartTime)
        val turnName = getTurnDisplayName(turn)
        binding.textTurnDisplay.text = getString(R.string.turn_display, turnName)
        binding.textTurnDisplay.visibility = View.VISIBLE
    }

    private fun determineTurnFromTime(startTime: String): Turn {
        val (hour, _) = startTime.split(":").map { it.toInt() }

        return when (hour) {
            in 0..11 -> Turn.MORNING
            else -> Turn.AFTERNOON
        }
    }

    private fun getTurnDisplayName(turn: Turn): String {
        return when (turn) {
            Turn.MORNING -> "Mañana"
            Turn.AFTERNOON -> "Tarde"
        }
    }

    private fun saveSchedule() {
        val courseName = binding.editTextCourseName.text.toString().trim()
        val courseCode = binding.editTextCourseCode.text.toString().trim()
        val classroom = binding.editTextClassroom.text.toString().trim()
        val professor = binding.editTextProfessor.text.toString().trim()

        if (courseName.isEmpty()) {
            binding.textInputCourseName.error = "El nombre de la materia es requerido"
            return
        }

        // VALIDAR QUE SE HAYAN SELECCIONADO AMBAS HORAS
        if (selectedStartTime.isEmpty() || selectedEndTime.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor selecciona el horario de inicio y fin", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedStartTime >= selectedEndTime) {
            Toast.makeText(requireContext(), "La hora de inicio debe ser antes de la hora de fin", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedDayPosition = binding.spinnerDay.selectedItemPosition
        selectedDay = ScheduleUtils.daysOfWeek[selectedDayPosition].second

        // DETERMINAR TURNO AUTOMÁTICAMENTE
        val autoDeterminedTurn = determineTurnFromTime(selectedStartTime)

        val newSchedule = Schedule(
            id = args.schedule?.id ?: "",
            courseName = courseName,
            courseCode = courseCode,
            dayOfWeek = selectedDay,
            startTime = selectedStartTime,
            endTime = selectedEndTime,
            turn = autoDeterminedTurn,
            classroom = classroom,
            professor = professor,
            color = getColorForCourse(courseName)
        )

        if (args.schedule != null) {
            viewModel.updateSchedule(newSchedule)
        } else {
            viewModel.addSchedule(newSchedule)
        }

        Toast.makeText(requireContext(), "Horario guardado", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    private fun getColorForCourse(courseName: String): Int {
        // Asignar colores diferentes basados en el nombre del curso
        val colors = listOf(
            R.color.purple_500,
            R.color.teal_500,
            R.color.orange_500,
            R.color.red_500,
            R.color.green_500,
            R.color.blue_500
        )
        val index = kotlin.math.abs(courseName.hashCode()) % colors.size
        return requireContext().getColor(colors[index])
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}