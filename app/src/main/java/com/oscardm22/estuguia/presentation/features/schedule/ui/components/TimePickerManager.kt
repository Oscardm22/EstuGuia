package com.oscardm22.estuguia.presentation.features.schedule.ui.components

import android.app.TimePickerDialog
import android.content.Context
import java.util.Calendar
import java.util.Locale

class TimePickerManager(
    private val context: Context,
    private val onTimeSelected: (String, Boolean) -> Unit
) {

    fun showTimePicker(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                val time = String.format(Locale.US, "%02d:%02d", selectedHour, selectedMinute)
                onTimeSelected(time, isStartTime)
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }
}