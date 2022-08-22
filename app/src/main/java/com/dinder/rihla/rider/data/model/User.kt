package com.dinder.rihla.rider.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = false)
    val id: String = "",
    val name: String,
    val phoneNumber: String,
    val token: String? = null
) {
    companion object {
        fun fromJson(json: Map<String, Any>): User {
            return User(
                id = json["id"].toString(),
                name = json["name"].toString(),
                phoneNumber = json["phoneNumber"].toString(),
                token = json["token"] as String? ?: ""
            )
        }
    }

    fun toJson(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "phoneNumber" to phoneNumber,
            "token" to token
        )
    }
}
