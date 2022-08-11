package com.dinder.rihla.rider.utils

import com.dinder.rihla.rider.common.Constants
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

@ExperimentalTime
object DateTimeUtils {
    fun getTimeInstance(formattedTime: String): Date {
        val timeInputs = formattedTime.split(":").map { it.toInt() }
        val date = Calendar.getInstance()
        date.set(Calendar.HOUR_OF_DAY, timeInputs[0])
        date.set(Calendar.MINUTE, timeInputs[1])
        return date.time
    }

    fun getDateInstance(formattedTime: String): Date {
        val timeInputs = formattedTime.split("/").map { it.toInt() }
        val date = Calendar.getInstance()
        date.set(timeInputs[2], timeInputs[1], timeInputs[0])
        return date.time
    }

    fun getFormattedDate(date: Date): String {
        val calendar = GregorianCalendar.getInstance().apply {
            this.time = date
        }

        return String.format(
            "%d-%d-%d",
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(GregorianCalendar.MONTH) + 1, // Calendar.MONTH is month order
            calendar.get(Calendar.YEAR)
        )
    }

    fun getFormattedTime(date: Date): String {
        val calendar = Calendar.getInstance().apply {
            this.time = date
        }

        return String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
            String.format("%02d", calendar.get(Calendar.MINUTE))
    }

    fun timeSince(date: Date): String {
        val milliSecondsDifference = date.time.milliseconds - Date().time.milliseconds
        return when {
            milliSecondsDifference.inWholeMilliseconds > Constants.DAY_MILLISECONDS -> {
                "${milliSecondsDifference.inWholeMilliseconds / Constants.DAY_MILLISECONDS} Days"
            }
            milliSecondsDifference.inWholeMilliseconds > Constants.HOUR_MILLISECONDS -> {
                "${milliSecondsDifference.inWholeMilliseconds / Constants.HOUR_MILLISECONDS} Hours"
            }

            else -> {
                "${milliSecondsDifference.inWholeMilliseconds / Constants.MINUTE_MILLISECONDS}" +
                    " Minutes"
            }
        }
    }
}
