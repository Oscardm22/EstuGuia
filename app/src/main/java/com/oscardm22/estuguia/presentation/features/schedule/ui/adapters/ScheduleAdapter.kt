package com.oscardm22.estuguia.presentation.features.schedule.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.oscardm22.estuguia.R
import com.oscardm22.estuguia.databinding.ItemScheduleBinding
import com.oscardm22.estuguia.domain.models.Schedule
import com.oscardm22.estuguia.domain.utils.ScheduleUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil

class ScheduleAdapter(
    private val onEditClick: (Schedule) -> Unit,
    private val onDeleteClick: (Schedule) -> Unit
) : ListAdapter<Schedule, ScheduleAdapter.ScheduleViewHolder>(ScheduleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemScheduleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = getItem(position)
        holder.bind(schedule)
    }

    inner class ScheduleViewHolder(
        private val binding: ItemScheduleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(schedule: Schedule) {
            with(binding) {
                textCourseName.text = schedule.courseName
                textCourseCode.text = schedule.courseCode

                textScheduleTime.text = root.context.getString(
                    R.string.schedule_time_format,
                    schedule.startTime,
                    schedule.endTime
                )

                textDay.text = ScheduleUtils.getDayName(schedule.dayOfWeek)
                textTurn.text = ScheduleUtils.getTurnName(schedule.turn)
                textClassroom.text = schedule.classroom
                textProfessor.text = schedule.professor

                // Color del curso
                val color = if (schedule.color != 0) schedule.color else
                    ContextCompat.getColor(root.context, R.color.purple_500)
                viewColorIndicator.setBackgroundColor(color)

                // Botones de acción
                buttonEdit.setOnClickListener {
                    onEditClick(schedule)
                }

                buttonDelete.setOnClickListener {
                    onDeleteClick(schedule)
                }

                root.setOnClickListener {
                    // Expandir/contraer detalles
                    val isExpanded = linearLayoutDetails.isVisible
                    linearLayoutDetails.visibility = if (isExpanded) View.GONE else View.VISIBLE
                    imageExpand.rotation = if (isExpanded) 0f else 180f
                }
            }
        }
    }

    class ScheduleDiffCallback : DiffUtil.ItemCallback<Schedule>() {
        override fun areItemsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
            return oldItem == newItem
        }
    }
}