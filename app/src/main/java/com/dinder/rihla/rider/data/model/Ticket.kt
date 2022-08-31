@file:OptIn(ExperimentalTime::class)

package com.dinder.rihla.rider.data.model

import com.dinder.rihla.rider.utils.DateTimeUtils
import java.util.Date
import kotlin.time.ExperimentalTime

// export interface Ticket {
//    id: string;
//    tripId: string;
//    time: Date;
//    date: Date;
//    from: Destination;
//    to: Destination;
//    promocode?: string;
//    rate: number;
//    passengerName: string;
//    passengerId: string;
//    passengerToken: string;
//    price: number;
//    status: SeatStatus;
// }

enum class TicketStatus {
    PRE_BOOK,
    PAYMENT_CONFIRMATION,
    PAID,
    CANCELLED,
    DISPROVED
}

data class Ticket(
    val id: String,
    val tripId: String,
    val company: Company,
    val departure: Date,
    val from: Destination,
    val to: Destination,
    val promoCode: String?,
    val rate: Double,
    val discountFactor: Double,
    val passengerId: String,
    val passengerName: String,
    val price: Int,
    val status: TicketStatus,
    val createdAt: Date,
    val seats: List<String>,
) {
    fun toJson(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "passengerId" to passengerId,
            "passengerName" to passengerName,
            "createdAt" to createdAt,
            "seats" to seats,
        )
    }

    companion object {
        fun fromJson(json: Map<String, Any>): Ticket {
            return Ticket(
                id = json["id"] as String,
                tripId = json["tripId"] as String,
                from = Destination.fromJson(json["from"] as Map<String, Any>),
                to = Destination.fromJson(json["to"] as Map<String, Any>),
                company = Company.fromJson(json["company"] as Map<String, Any>),
                promoCode = json["promoCode"] as String?,
                rate = json["rate"] as Double,
                discountFactor = json["discountFactor"] as Double,
                passengerId = json["passengerId"] as String,
                passengerName = json["passengerName"] as String,
                price = json["price"].toString().toInt(),
                status = TicketStatus.valueOf(json["status"] as String),
                createdAt = DateTimeUtils.decodeTimeStamp(json["createdAt"]),
                departure = DateTimeUtils.decodeTimeStamp(json["departure"]),
                seats = json["seats"] as List<String>
            )
        }
    }
}
