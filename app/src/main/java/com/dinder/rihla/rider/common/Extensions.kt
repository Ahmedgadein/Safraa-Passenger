package com.dinder.rihla.rider.common

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

fun Context.color(resId: Int): Int =
    ResourcesCompat.getColor(this.resources, resId, this.theme)

fun Date.dayOfWeek(): String {
    val calendar = GregorianCalendar.getInstance()
    calendar.time = this

    val isArabic = Locale.getDefault().language.equals(Locale("ar").language)

    return when (calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.SUNDAY -> if (isArabic) "ألأحد" else "Sun"
        Calendar.MONDAY -> if (isArabic) "الإثنين" else "Mon"
        Calendar.TUESDAY -> if (isArabic) "الثلاثاء" else "Tue"
        Calendar.WEDNESDAY -> if (isArabic) "الأربعاء" else "Wed"
        Calendar.THURSDAY -> if (isArabic) "الخميس" else "Thu"
        Calendar.FRIDAY -> if (isArabic) "الجمعة" else "Fri"
        else -> if (isArabic) "السبت" else "Sat"
    }
}
