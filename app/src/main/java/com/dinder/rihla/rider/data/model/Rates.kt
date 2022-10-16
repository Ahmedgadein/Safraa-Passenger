package com.dinder.rihla.rider.data.model

import com.dinder.rihla.rider.common.Fields

data class Rates(val agent: Double, val passenger: Double) {
    companion object {
        fun fromJson(json: Map<String, Any>): Rates =
            Rates(
                agent = json[Fields.AGENT] as Double,
                passenger = json[Fields.PASSENGER] as Double
            )
    }
}
