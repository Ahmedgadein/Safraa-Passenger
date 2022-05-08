package com.dinder.rihla.rider.utils

object PhoneNumberFormatter {
    fun getFullNumber(phoneNumber: String, countryCode: String = "+249"): String =
        countryCode + phoneNumber
}
