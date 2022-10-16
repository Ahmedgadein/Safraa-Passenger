package com.dinder.rihla.rider.utils

import com.dinder.rihla.rider.data.model.Ticket
import com.dinder.rihla.rider.data.model.Trip
import kotlin.math.roundToInt

object PriceUtils {
    fun getBasePrice(ticket: Ticket) =
        ((1.0 + ticket.rate) * ticket.price * ticket.seats.size).roundToInt().toString()

    fun getPrice(ticket: Ticket): String = if (ticket.promoCode.isNullOrEmpty()) {
        getBasePrice(ticket)
    } else {
        (ticket.price * ticket.seats.size * ticket.discountFactor).roundToInt()
            .toString()
    }

    fun getPrice(trip: Trip): String =
        (trip.price * (1 + trip.rate)).roundToInt()
            .toString()

    fun getCommission(trip: Trip): String = (trip.price * trip.rate)
        .roundToInt()
        .toString()
}
