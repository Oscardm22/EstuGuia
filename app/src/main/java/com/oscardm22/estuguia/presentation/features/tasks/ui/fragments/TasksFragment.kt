package com.oscardm22.estuguia.presentation.features.tasks.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.oscardm22.estuguia.databinding.FragmentTasksBinding
import com.oscardm22.estuguia.presentation.features.tasks.ui.adapters.TaskAdapter
import com.oscardm22.estuguia.presentation.features.tasks.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by viewModels()
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

        // Cargar tareas
        val userId = getCurrentUserId()
        viewModel.loadTasks(userId)
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
                val updatedTask = task.copy(status = newStatus)
                val userId = getCurrentUserId()
                viewModel.updateTask(updatedTask, userId)
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
                    // Mostrar error
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
            // Navegar al fragment de agregar tarea
            navigateToAddTask()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            val userId = getCurrentUserId()
            viewModel.loadTasks(userId)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun getCurrentUserId(): String {
        // Implementar - obtener del usuario autenticado
        // Por ejemplo: FirebaseAuth.getInstance().currentUser?.uid ?: ""
        return "user_id_placeholder"
    }

    private fun showError(message: String) {
        // Mostrar snackbar o toast con el error
    }

    private fun navigateToAddTask() {
        // Navegar a AddTaskFragment
        // requireActivity().supportFragmentManager.beginTransaction()...
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}