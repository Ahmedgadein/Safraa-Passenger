package com.dinder.rihla.rider.data.model

import com.dinder.rihla.rider.utils.SeatUtils
import com.dinder.rihlabus.data.model.Seat
import com.google.firebase.Timestamp
import java.util.Date

data class Trip(
    val id: Long? = null,
    val date: Date,
    val time: Date,
    val company: Company,
    val from: Destination,
    val to: Destination,
    val price: Int,
    val seats: List<Seat>
) {

    companion object {
        fun fromJson(json: Map<String, Any>) = Trip(
            id = json["id"] as Long,
            date = (json["date"] as Timestamp).toDate(),
            time = (json["time"] as Timestamp).toDate(),
            company = Company.fromJson(json["company"] as Map<String, Any>),
            from = Destination.fromJson(json["from"] as Map<String, Any>),
            to = Destination.fromJson(json["to"] as Map<String, Any>),
            price = json["price"].toString().toInt(),
            seats = SeatUtils.seatsMapToModel(json["seats"] as Map<String, Map<String, Any?>>)
        )
    }
}
