package com.oscardm22.estuguia.presentation.features.tasks.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.oscardm22.estuguia.R
import com.oscardm22.estuguia.databinding.ItemTaskBinding
import com.oscardm22.estuguia.domain.models.Task
import com.oscardm22.estuguia.domain.models.TaskPriority
import com.oscardm22.estuguia.domain.models.TaskStatus

class TaskAdapter(
    private val onEditClick: (Task) -> Unit,
    private val onDeleteClick: (Task) -> Unit,
    private val onStatusChange: (Task, TaskStatus) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    inner class TaskViewHolder(
        private val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.apply {
                checkboxCompleted.setOnCheckedChangeListener(null)

                textTaskTitle.text = task.title
                textTaskDescription.text = task.description
                textDueDate.text = formatDate(task.dueDate)
                textPriority.text = getPriorityText(task.priority)

                setPriorityStyle(task.priority)
                setStatusStyle(task.status)

                checkboxCompleted.isChecked = task.status == TaskStatus.COMPLETED

                // Listeners
                root.setOnClickListener {
                    onEditClick(task)
                }

                // Listener para el botón de opciones
                btnOptions.setOnClickListener {
                    showOptionsMenu(task)
                }

                checkboxCompleted.setOnCheckedChangeListener { _, isChecked ->
                    val newStatus = if (isChecked) TaskStatus.COMPLETED else TaskStatus.PENDING
                    onStatusChange(task, newStatus)
                }
            }
        }

        private fun showOptionsMenu(task: Task) {
            val options = arrayOf("Editar", "Eliminar")

            androidx.appcompat.app.AlertDialog.Builder(binding.root.context)
                .setTitle("Opciones de tarea")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> onEditClick(task)
                        1 -> onDeleteClick(task)
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        private fun getPriorityText(priority: TaskPriority): String {
            return when (priority) {
                TaskPriority.LOW -> binding.root.context.getString(R.string.priority_low)
                TaskPriority.MEDIUM -> binding.root.context.getString(R.string.priority_medium)
                TaskPriority.HIGH -> binding.root.context.getString(R.string.priority_high)
            }
        }

        private fun setPriorityStyle(priority: TaskPriority) {
            when (priority) {
                TaskPriority.HIGH -> {
                    binding.textPriority.setBackgroundResource(R.drawable.bg_priority_high)
                    binding.textPriority.setTextColor(getColor(R.color.priority_high_text))
                }
                TaskPriority.MEDIUM -> {
                    binding.textPriority.setBackgroundResource(R.drawable.bg_priority_medium)
                    binding.textPriority.setTextColor(getColor(R.color.priority_medium_text))
                }
                TaskPriority.LOW -> {
                    binding.textPriority.setBackgroundResource(R.drawable.bg_priority_low)
                    binding.textPriority.setTextColor(getColor(R.color.priority_low_text))
                }
            }
        }

        private fun setStatusStyle(status: TaskStatus) {
            when (status) {
                TaskStatus.COMPLETED -> {
                    binding.root.alpha = 0.6f
                    binding.textTaskTitle.paintFlags = android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                }
                else -> {
                    binding.root.alpha = 1.0f
                    binding.textTaskTitle.paintFlags = 0
                }
            }
        }

        private fun formatDate(date: java.util.Date): String {
            val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            return formatter.format(date)
        }

        private fun getColor(colorRes: Int): Int {
            return binding.root.context.getColor(colorRes)
        }
    }

    companion object {
        private val TaskDiffCallback = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem == newItem
            }
        }
    }
}