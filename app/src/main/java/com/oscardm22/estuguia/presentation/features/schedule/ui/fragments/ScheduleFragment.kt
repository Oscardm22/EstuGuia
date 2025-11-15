package com.oscardm22.estuguia.presentation.features.schedule.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.oscardm22.estuguia.R
import com.oscardm22.estuguia.databinding.FragmentScheduleBinding
import com.oscardm22.estuguia.domain.models.Schedule
import com.oscardm22.estuguia.presentation.features.schedule.ui.adapters.ScheduleAdapter
import com.oscardm22.estuguia.presentation.features.schedule.viewmodel.ScheduleStats
import com.oscardm22.estuguia.presentation.features.schedule.viewmodel.ScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ScheduleViewModel by viewModels()

    private lateinit var scheduleAdapter: ScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        viewModel.getSchedules()
    }

    private fun setupRecyclerView() {
        scheduleAdapter = ScheduleAdapter(
            onEditClick = { schedule ->
                navigateToAddSchedule(schedule)
            },
            onDeleteClick = { schedule ->
                showDeleteConfirmation(schedule)
            }
        )

        binding.recyclerViewSchedules.apply {
            adapter = scheduleAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.scheduleState.collect { state ->
                when {
                    state.isLoading -> showLoading()
                    state.error != null -> showError(state.error)
                    else -> {
                        showSchedules(state.schedules)
                        updateStats(state.stats)
                    }
                }
            }
        }
    }

    private fun updateStats(stats: ScheduleStats) {
        binding.textClassesCount.text = stats.totalClasses.toString()
        binding.textDaysCount.text = stats.uniqueDays.toString()
        binding.textTurnsCount.text = stats.uniqueTurns.toString()
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerViewSchedules.visibility = View.GONE
        binding.textEmpty.visibility = View.GONE
    }

    private fun showError(error: String) {
        binding.progressBar.visibility = View.GONE
        binding.recyclerViewSchedules.visibility = View.GONE
        binding.textEmpty.visibility = View.VISIBLE
        binding.textEmpty.text = error

        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }

    private fun showSchedules(schedules: List<Schedule>) {
        binding.progressBar.visibility = View.GONE

        if (schedules.isEmpty()) {
            binding.recyclerViewSchedules.visibility = View.GONE
            binding.textEmpty.visibility = View.VISIBLE
            binding.textEmpty.text = getString(R.string.no_schedules)
        } else {
            binding.recyclerViewSchedules.visibility = View.VISIBLE
            binding.textEmpty.visibility = View.GONE
            scheduleAdapter.submitList(schedules)
        }
    }

    private fun setupClickListeners() {
        binding.fabAddSchedule.setOnClickListener {
            navigateToAddSchedule()
        }
    }

    private fun navigateToAddSchedule(schedule: Schedule? = null) {
        try {
            // Usar Safe Args para navegar
            val directions = ScheduleFragmentDirections.actionScheduleFragmentToAddScheduleFragment(
                schedule = schedule
            )

            // Realizar la navegación
            findNavController().navigate(directions)

        } catch (e: Exception) {
            // Manejar error de navegación
            Toast.makeText(
                requireContext(),
                "Error al navegar: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }

    private fun showDeleteConfirmation(schedule: Schedule) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar horario")
            .setMessage("¿Estás seguro de que quieres eliminar ${schedule.courseName}?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteSchedule(schedule.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}