package com.dinder.rihla.rider.data.model

import com.dinder.rihla.rider.utils.SeatUtils
import com.google.firebase.Timestamp
import java.util.Date

data class Trip(
    val id: String? = null,
    val departure: Date,
    val company: Company,
    val from: Destination,
    val to: Destination,
    val rate: Double,
    val price: Int,
    val agentRate: Double = 0.0,
    val seats: List<Seat>
) {

    companion object {
        fun fromJson(json: Map<String, Any>) = Trip(
            id = json["id"] as String?,
            departure = (json["departure"] as Timestamp).toDate(),
            company = Company.fromJson(json["company"] as Map<String, Any>),
            from = Destination.fromJson(json["from"] as Map<String, Any>),
            rate = json["rate"] as Double? ?: 0.0,
            to = Destination.fromJson(json["to"] as Map<String, Any>),
            price = json["price"].toString().toInt(),
            seats = SeatUtils.seatsMapToModel(json["seats"] as Map<String, Map<String, Any?>>)
        )
    }
}
