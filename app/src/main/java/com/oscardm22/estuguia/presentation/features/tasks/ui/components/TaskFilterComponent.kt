package com.oscardm22.estuguia.presentation.features.tasks.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.google.android.material.chip.Chip
import com.oscardm22.estuguia.databinding.ComponentTaskFilterBinding
import com.oscardm22.estuguia.domain.models.TaskStatus

class TaskFilterComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ComponentTaskFilterBinding = ComponentTaskFilterBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )
    private var onFilterChange: ((TaskStatus?) -> Unit)? = null

    init {
        setupClickListeners()
    }

    fun setOnFilterChangeListener(listener: (TaskStatus?) -> Unit) {
        onFilterChange = listener
    }

    private fun setupClickListeners() {
        binding.chipAll.setOnClickListener {
            setActiveChip(binding.chipAll)
            onFilterChange?.invoke(null)
        }

        binding.chipPending.setOnClickListener {
            setActiveChip(binding.chipPending)
            onFilterChange?.invoke(TaskStatus.PENDING)
        }

        binding.chipInProgress.setOnClickListener {
            setActiveChip(binding.chipInProgress)
            onFilterChange?.invoke(TaskStatus.IN_PROGRESS)
        }

        binding.chipCompleted.setOnClickListener {
            setActiveChip(binding.chipCompleted)
            onFilterChange?.invoke(TaskStatus.COMPLETED)
        }
    }

    private fun setActiveChip(activeChip: Chip) {
        listOf(
            binding.chipAll,
            binding.chipPending,
            binding.chipInProgress,
            binding.chipCompleted
        ).forEach { chip ->
            chip.isChecked = chip == activeChip
        }
    }

    fun clearFilters() {
        setActiveChip(binding.chipAll)
        onFilterChange?.invoke(null)
    }
}