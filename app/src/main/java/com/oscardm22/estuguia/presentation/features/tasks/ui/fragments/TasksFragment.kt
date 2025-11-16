package com.oscardm22.estuguia.presentation.features.tasks.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.oscardm22.estuguia.R
import com.oscardm22.estuguia.databinding.FragmentTasksBinding
import com.oscardm22.estuguia.domain.repositories.AuthRepository
import com.oscardm22.estuguia.presentation.features.tasks.ui.adapters.TaskAdapter
import com.oscardm22.estuguia.presentation.features.tasks.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by viewModels()

    @Inject
    lateinit var authRepository: AuthRepository

    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupFilterListeners()

        viewLifecycleOwner.lifecycleScope.launch {
            val userId = getCurrentUserId()
            if (userId != null) {
                viewModel.loadTasks(userId)
            } else {
                // Manejar caso de usuario no autenticado
                showError("Usuario no autenticado")
            }
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskClick = { task ->
                // Navegar a detalle o edición
            },
            onTaskLongClick = { task ->
                // Mostrar opciones (editar, eliminar, etc.)
            },
            onStatusChange = { task, newStatus ->
                // Actualizar estado de la tarea
                viewLifecycleOwner.lifecycleScope.launch {
                    val userId = getCurrentUserId()
                    if (userId != null) {
                        val updatedTask = task.copy(status = newStatus)
                        viewModel.updateTask(updatedTask, userId)
                    }
                }
            }
        )

        binding.recyclerViewTasks.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                if (state.error != null) {
                    showError(state.error)
                }

                taskAdapter.submitList(state.tasks)

                if (state.tasks.isEmpty() && !state.isLoading) {
                    binding.emptyState.visibility = View.VISIBLE
                    binding.recyclerViewTasks.visibility = View.GONE
                } else {
                    binding.emptyState.visibility = View.GONE
                    binding.recyclerViewTasks.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabAddTask.setOnClickListener {
            navigateToAddTask()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val userId = getCurrentUserId()
                if (userId != null) {
                    viewModel.loadTasks(userId)
                }
                binding.swipeRefreshLayout.isRefreshing = false
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

    private fun showError(message: String) {
        // Mostrar snackbar o toast con el error
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun navigateToAddTask() {
        val action = TasksFragmentDirections.actionTasksFragmentToAddTaskFragment()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupFilterListeners() {
        val chipGroup = binding.filterComponent.root.findViewById<com.google.android.material.chip.ChipGroup>(R.id.chipGroup)

        // Configurar listener del ChipGroup para manejar la selección automática
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            when (checkedIds.firstOrNull()) {
                R.id.chipAll -> applyFilter(null)
                R.id.chipPending -> applyFilter("pending")
                R.id.chipInProgress -> applyFilter("in_progress")
                R.id.chipCompleted -> applyFilter("completed")
                else -> applyFilter(null)
            }
        }

        chipGroup.check(R.id.chipAll)
    }

    private fun applyFilter(status: String?) {
        viewLifecycleOwner.lifecycleScope.launch {
            val userId = getCurrentUserId()
            if (userId != null) {
                if (status == null) {
                    viewModel.loadTasks(userId) // Todas las tareas
                } else {
                    viewModel.getTasksByStatus(userId, status) // Filtrado por estado
                }
            }
        }
    }
}