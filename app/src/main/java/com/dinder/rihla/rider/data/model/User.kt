package com.dinder.rihla.rider.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Role {
    PASSENGER,
    AGENT
}

@Entity
data class User(
    @PrimaryKey(autoGenerate = false)
    val id: String = "",
    val name: String,
    @ColumnInfo(defaultValue = "PASSENGER")
    val role: Role,
    val phoneNumber: String,
    val token: String? = null,
    // read-only fields
    @ColumnInfo(defaultValue = "1")
    val active: Boolean = true,
    @ColumnInfo(name = "points", defaultValue = "0", typeAffinity = ColumnInfo.INTEGER)
    val points: Int = 0
) {
    companion object {
        fun fromJson(json: Map<String, Any>): User {
            return User(
                id = json["id"].toString(),
                name = json["name"].toString(),
                role = Role.valueOf(json["role"].toString()),
                phoneNumber = json["phoneNumber"].toString(),
                token = json["token"] as String? ?: "",
                active = json["active"] as Boolean? ?: true,
                points = json["points"].toString().toIntOrNull() ?: 0
            )
        }
    }

    fun toJson(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "role" to role,
            "phoneNumber" to phoneNumber,
            "token" to token
        )
    }
}
