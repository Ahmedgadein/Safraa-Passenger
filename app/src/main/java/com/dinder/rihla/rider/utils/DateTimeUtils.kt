package com.dinder.rihla.rider.utils

import android.content.res.Resources
import com.dinder.rihla.rider.R
import com.dinder.rihla.rider.common.Constants
import com.dinder.rihla.rider.common.dayOfWeek
import com.google.firebase.Timestamp
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

@ExperimentalTime
object DateTimeUtils {
    fun getFormattedDate(date: Date): String {
        val calendar = GregorianCalendar.getInstance().apply {
            this.time = date
        }

        val isArabic = Locale.getDefault().language.equals(Locale("ar").language)
        return String.format(
            if (isArabic) "%s \n %d - %d - %d" else "%s \n%d-%d-%d",
            date.dayOfWeek(),
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

    fun departureWithin(date: Date, resources: Resources): String {
        val milliSecondsDifference =
            date.time.milliseconds.inWholeMilliseconds - Date().time.milliseconds.inWholeMilliseconds

        if (milliSecondsDifference <= 0) {
            return resources.getString(R.string.past_ticket)
        } else {

            return when {
                milliSecondsDifference > Constants.DAY_MILLISECONDS -> {
                    val days = milliSecondsDifference / Constants.DAY_MILLISECONDS
                    return if (Locale.getDefault().language.equals(Locale("ar").language)) {
                        when (days) {
                            1L -> "يوم"
                            2L -> "يومين"
                            in 3L..10L -> "$days أيام"
                            else -> "$days يوم"
                        }
                    } else {
                        when (days) {
                            1L -> "a day"
                            else -> "$days days"
                        }
                    }
                }

                milliSecondsDifference > Constants.HOUR_MILLISECONDS -> {
                    val hours = milliSecondsDifference / Constants.HOUR_MILLISECONDS
                    return if (Locale.getDefault().language.equals(Locale("ar").language)) {
                        when (hours) {
                            1L -> "ساعة"
                            2L -> "ساعتين"
                            in 3L..10L -> "$hours ساعات"
                            else -> "$hours ساعة"
                        }
                    } else {
                        when (hours) {
                            1L -> "an hour"
                            else -> "$hours hours"
                        }
                    }
                }

                else -> {
                    val minutes = milliSecondsDifference / Constants.MINUTE_MILLISECONDS
                    return if (Locale.getDefault().language.equals(Locale("ar").language)) {
                        when (minutes) {
                            1L -> "دقيقة"
                            2L -> "دقيقتين"
                            in 3L..10L -> "$minutes دقائق"
                            else -> "$minutes دقيقة"
                        }
                    } else {
                        when (minutes) {
                            1L -> "a minute"
                            else -> "$minutes minutes"
                        }
                    }
                }
            }
        }
    }

    fun decodeTimeStamp(timestamp: Any?): Date {
        return if (timestamp is Timestamp) {
            timestamp.toDate()
        } else {
            val seconds = (timestamp as Map<String, Int>)["_seconds"]!!
            Date(seconds * 1000L)
        }
    }
}
