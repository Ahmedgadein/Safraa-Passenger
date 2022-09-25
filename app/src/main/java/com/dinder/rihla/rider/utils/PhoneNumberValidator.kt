package com.dinder.rihla.rider.utils

import android.content.Context
import com.dinder.rihla.rider.R

object PhoneNumberValidator {
    fun validate(phoneNumber: String?, context: Context): String? {
        return when {
            phoneNumber.isNullOrEmpty() -> context.resources.getString(R.string.required)

            !phoneNumber.matches(Regex("[0-9]{9}")) ->
                context.resources.getString(R.string.should_be_9_numbers)

            !phoneNumber.matches(Regex("[1|9][0-9]{8}")) ->
                context.resources.getString(R.string.invalid_number)

            else -> {
                null
            }
        }
    }
}
