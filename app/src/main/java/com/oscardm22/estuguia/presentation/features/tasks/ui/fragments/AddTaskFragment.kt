package com.oscardm22.estuguia.presentation.features.tasks.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.oscardm22.estuguia.databinding.FragmentAddTaskBinding
import com.oscardm22.estuguia.domain.models.Task
import com.oscardm22.estuguia.domain.models.TaskPriority
import com.oscardm22.estuguia.domain.models.TaskStatus
import com.oscardm22.estuguia.presentation.features.tasks.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class AddTaskFragment : Fragment() {

    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by viewModels()

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

        setupClickListeners()
        setupObservers()
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

        // Listeners para selectores de fecha, prioridad, etc.
        setupDatePickers()
        setupPrioritySelector()
    }

    private fun setupObservers() {
        // Observar estado del ViewModel para loading/errores
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
        val scheduleId = ""
        val dueDate = Date()
        val priority = TaskPriority.MEDIUM
        val reminderTime: Date? = null

        val newTask = Task(
            title = title,
            description = description,
            scheduleId = scheduleId,
            dueDate = dueDate,
            priority = priority,
            status = TaskStatus.PENDING,
            reminderTime = reminderTime
        )

        val userId = getCurrentUserId()
        viewModel.addTask(newTask, userId)

        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun setupDatePickers() {
    }

    private fun setupPrioritySelector() {
    }

    private fun getCurrentUserId(): String {
        return "user_id_placeholder"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}