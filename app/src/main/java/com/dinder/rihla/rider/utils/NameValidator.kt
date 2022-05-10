package com.dinder.rihla.rider.utils

object NameValidator {
    fun validate(name: String?): String? {
        if (name.isNullOrEmpty()) {
            return "Required"
        }

        if (name.split(" ").size < 2) {
            return "Full name required"
        }

        return null
    }
}
