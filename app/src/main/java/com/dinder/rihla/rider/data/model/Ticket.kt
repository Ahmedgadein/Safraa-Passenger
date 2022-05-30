package com.dinder.rihla.rider.data.model

data class Ticket(
    val id: String? = null,
    val passengerId: String,
    val passengerName: String,
    val seats: List<String>,
    val trip: Trip
) {
    fun toJson(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "passengerId" to passengerId,
            "passengerName" to passengerName,
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
                seats = json["seats"] as List<String>,
                trip = Trip.fromTicketJson(json["trip"] as Map<String, Any>)
            )
        }
    }
}
