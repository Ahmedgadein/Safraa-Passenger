package com.dinder.rihla.rider.data.model

import com.google.firebase.Timestamp
import java.util.Date

data class Ticket(
    val id: String? = null,
    val passengerId: String,
    val passengerName: String,
    val createdAt: Date? = null,
    val seats: List<String>,
    val trip: Trip
) {
    fun toJson(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "passengerId" to passengerId,
            "passengerName" to passengerName,
            "createdAt" to createdAt,
            "seats" to seats,
            "trip" to trip.toTicketJson()
        )
    }

    companion object {
        fun fromJson(json: Map<String, Any>): Ticket {
            return Ticket(
                id = json["id"].toString(),
                passengerId = json["passengerId"].toString(),
                passengerName = json["passengerName"].toString(),
                createdAt = (json["createdAt"] as Timestamp).toDate(),
                seats = json["seats"] as List<String>,
                trip = Trip.fromTicketJson(json["trip"] as Map<String, Any>)
            )
        }
    }
}
