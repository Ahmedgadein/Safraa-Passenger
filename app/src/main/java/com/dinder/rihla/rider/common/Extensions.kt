package com.dinder.rihla.rider.common

import android.content.Context
import androidx.core.content.res.ResourcesCompat

fun Context.color(resId: Int): Int =
    ResourcesCompat.getColor(this.resources, resId, this.theme)
