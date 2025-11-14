package com.oscardm22.estuguia.presentation.features.tasks.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.google.android.material.chip.Chip
import com.oscardm22.estuguia.databinding.ComponentPrioritySelectorBinding
import com.oscardm22.estuguia.domain.models.TaskPriority

class PriorityChipGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ComponentPrioritySelectorBinding = ComponentPrioritySelectorBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )
    private var onPrioritySelected: ((TaskPriority) -> Unit)? = null
    private var selectedPriority: TaskPriority = TaskPriority.MEDIUM

    init {
        setupClickListeners()
        setActiveChip(binding.chipMedium)
    }

    fun setOnPrioritySelectedListener(listener: (TaskPriority) -> Unit) {
        this.onPrioritySelected = listener
    }

    fun getSelectedPriority(): TaskPriority {
        return selectedPriority
    }

    private fun setupClickListeners() {
        binding.chipLow.setOnClickListener {
            selectedPriority = TaskPriority.LOW
            setActiveChip(binding.chipLow)
            onPrioritySelected?.invoke(selectedPriority)
        }

        binding.chipMedium.setOnClickListener {
            selectedPriority = TaskPriority.MEDIUM
            setActiveChip(binding.chipMedium)
            onPrioritySelected?.invoke(selectedPriority)
        }

        binding.chipHigh.setOnClickListener {
            selectedPriority = TaskPriority.HIGH
            setActiveChip(binding.chipHigh)
            onPrioritySelected?.invoke(selectedPriority)
        }
    }

    private fun setActiveChip(activeChip: Chip) {
        listOf(
            binding.chipLow,
            binding.chipMedium,
            binding.chipHigh
        ).forEach { chip ->
            chip.isChecked = chip == activeChip
        }
    }

    fun setPriority(priority: TaskPriority) {
        selectedPriority = priority
        when (priority) {
            TaskPriority.LOW -> setActiveChip(binding.chipLow)
            TaskPriority.MEDIUM -> setActiveChip(binding.chipMedium)
            TaskPriority.HIGH -> setActiveChip(binding.chipHigh)
        }
    }
}