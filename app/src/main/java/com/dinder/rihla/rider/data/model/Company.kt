package com.dinder.rihla.rider.data.model

data class Company(val name: String, val arabicName: String) {

    fun toJson(): Map<String, Any> = mapOf(
        "name" to name,
        "arabicName" to arabicName
    )

    companion object {
        fun fromJson(json: Map<String, Any>): Company {
            return Company(
                name = json["name"].toString(),
                arabicName = json["arabicName"].toString()
            )
        }
    }
}
