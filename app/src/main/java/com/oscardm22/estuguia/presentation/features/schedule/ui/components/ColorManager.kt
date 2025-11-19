package com.oscardm22.estuguia.presentation.features.schedule.ui.components

import android.content.Context
import com.oscardm22.estuguia.R

class ColorManager(private val context: Context) {

    fun getColorForCourse(courseName: String): Int {
        val colors = listOf(
            R.color.purple_500,
            R.color.teal_500,
            R.color.orange_500,
            R.color.red_500,
            R.color.green_500,
            R.color.blue_500
        )
        val index = kotlin.math.abs(courseName.hashCode()) % colors.size
        return context.getColor(colors[index])
    }
}