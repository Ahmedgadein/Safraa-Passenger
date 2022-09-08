package com.dinder.rihla.rider.data.model

import com.dinder.rihla.rider.common.Fields

data class UpdateApp(val version: String, val updateRequired: Boolean) {
    companion object {
        fun fromJson(json: Map<String, Any>): UpdateApp {
            return UpdateApp(
                version = json[Fields.VERSION].toString(),
                updateRequired = json[Fields.UPDATE_REQUIRED] as Boolean
            )
        }
    }
}
