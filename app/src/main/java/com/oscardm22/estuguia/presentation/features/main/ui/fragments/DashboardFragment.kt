package com.oscardm22.estuguia.presentation.features.main.ui.fragments

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
import com.oscardm22.estuguia.databinding.FragmentDashboardBinding
import com.oscardm22.estuguia.presentation.features.main.ui.adapters.UpcomingTasksAdapter
import com.oscardm22.estuguia.presentation.features.main.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var upcomingTasksAdapter: UpcomingTasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUpcomingTasksRecyclerView()
        setupClickListeners()
        setupObservers()
        updateGreeting()
    }

    private fun setupUpcomingTasksRecyclerView() {
        upcomingTasksAdapter = UpcomingTasksAdapter { task ->
            // Navegar a los detalles de la tarea o a la lista de tareas
            navigateToTasks()
        }

        binding.recyclerUpcomingTasks.apply {
            adapter = upcomingTasksAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        binding.buttonViewAllTasks.setOnClickListener {
            navigateToTasks()
        }

        binding.buttonAddClass.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_schedule)
        }

        binding.buttonAddTask.setOnClickListener {
            navigateToTasks()
        }
    }

    private fun setupObservers() {
        // Observar datos del usuario con StateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userData.collect { user ->
                binding.textUserName.text = user.name
            }
        }

        // Observar estadísticas con StateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dashboardStats.collect { stats ->
                // Actualizar contadores básicos
                binding.textClassesToday.text = stats.todayClasses.toString()
                binding.textPendingTasks.text = stats.pendingTasks.toString()
                binding.textNextClass.text = stats.nextClassTime ?: getString(R.string.default_time)

                // Actualizar próximas tareas
                updateUpcomingTasksSection(stats.upcomingTasks)
            }
        }
    }

    private fun updateUpcomingTasksSection(upcomingTasks: List<com.oscardm22.estuguia.domain.models.Task>) {
        if (upcomingTasks.isEmpty()) {
            // Ocultar el RecyclerView cuando no hay tareas
            binding.recyclerUpcomingTasks.visibility = View.GONE
        } else {
            // Mostrar el RecyclerView cuando hay tareas
            binding.recyclerUpcomingTasks.visibility = View.VISIBLE

            // Mostrar máximo 5 tareas en el dashboard
            val tasksToShow = upcomingTasks.take(5)
            upcomingTasksAdapter.submitList(tasksToShow)
        }
    }

    private fun updateGreeting() {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 0..11 -> "¡Buenos días!"
            in 12..18 -> "¡Buenas tardes!"
            else -> "¡Buenas noches!"
        }
        binding.textGreeting.text = greeting
    }

    private fun navigateToTasks() {
        findNavController().navigate(R.id.action_dashboard_to_tasks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}