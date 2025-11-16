package com.oscardm22.estuguia.presentation.features.main.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.oscardm22.estuguia.R
import com.oscardm22.estuguia.databinding.ItemUpcomingTaskBinding
import com.oscardm22.estuguia.domain.models.Task
import com.oscardm22.estuguia.domain.models.TaskPriority
import java.text.SimpleDateFormat
import java.util.*

class UpcomingTasksAdapter(
    private val onTaskClick: (Task) -> Unit
) : ListAdapter<Task, UpcomingTasksAdapter.UpcomingTaskViewHolder>(UpcomingTaskDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingTaskViewHolder {
        val binding = ItemUpcomingTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UpcomingTaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UpcomingTaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    inner class UpcomingTaskViewHolder(
        private val binding: ItemUpcomingTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.apply {
                textTaskTitle.text = task.title
                textDueDate.text = binding.root.context.getString(
                    R.string.due_date_format,
                    formatDate(task.dueDate)
                )
                textPriority.text = getPriorityText(task.priority)

                // Establecer estilo según prioridad
                setPriorityStyle(task.priority)

                root.setOnClickListener {
                    onTaskClick(task)
                }
            }
        }

        private fun setPriorityStyle(priority: TaskPriority) {
            when (priority) {
                TaskPriority.HIGH -> {
                    binding.textPriority.setBackgroundResource(R.drawable.bg_priority_high)
                    binding.textPriority.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.priority_high_text)
                    )
                }
                TaskPriority.MEDIUM -> {
                    binding.textPriority.setBackgroundResource(R.drawable.bg_priority_medium)
                    binding.textPriority.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.priority_medium_text)
                    )
                }
                TaskPriority.LOW -> {
                    binding.textPriority.setBackgroundResource(R.drawable.bg_priority_low)
                    binding.textPriority.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.priority_low_text)
                    )
                }
            }
        }

        private fun getPriorityText(priority: TaskPriority): String {
            return when (priority) {
                TaskPriority.HIGH -> binding.root.context.getString(R.string.priority_high)
                TaskPriority.MEDIUM -> binding.root.context.getString(R.string.priority_medium)
                TaskPriority.LOW -> binding.root.context.getString(R.string.priority_low)
            }
        }

        private fun formatDate(date: Date): String {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return formatter.format(date)
        }
    }

    companion object {
        private val UpcomingTaskDiffCallback = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem == newItem
            }
        }
    }
}