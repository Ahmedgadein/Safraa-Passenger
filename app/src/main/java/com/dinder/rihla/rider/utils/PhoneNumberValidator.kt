package com.dinder.rihla.rider.utils

object PhoneNumberValidator {
    fun validate(phoneNumber: String?): String? {
        return when {
            phoneNumber.isNullOrEmpty() -> "Required"

            !phoneNumber.matches(Regex("[0-9]{9}")) -> {
                "Should be 9 numbers"
            }
            !phoneNumber.matches(Regex("[1|9][0-9]{8}")) -> {
                "Invalid number"
            }
            else -> {
                null
            }
        }
    }
}
