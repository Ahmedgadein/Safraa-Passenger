package com.dinder.rihla.rider.utils

import android.content.Context
import com.dinder.rihla.rider.R

object NameValidator {
    fun validate(name: String?, context: Context): String? {
        if (name.isNullOrEmpty()) {
            return context.getString(R.string.required)
        }

        if (name.split(" ").size < 2) {
            return context.getString(R.string.fullname_required)
        }

        return null
    }
}
