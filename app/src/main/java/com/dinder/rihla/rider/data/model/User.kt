package com.dinder.rihla.rider.data.model

data class User(
    val id: String,
    val name: String,
    val phoneNumber: String
) {
    companion object {
        fun fromJson(json: Map<String, Any>): User {
            return User(
                id = json["id"].toString(),
                name = json["name"].toString(),
                phoneNumber = json["phoneNumber"].toString(),
            )
        }
    }

    fun toJson(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "phoneNumber" to phoneNumber,
        )
    }
}
