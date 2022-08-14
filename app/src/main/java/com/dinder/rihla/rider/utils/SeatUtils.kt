package com.dinder.rihla.rider.utils

import com.dinder.rihla.rider.common.Constants.NUMBER_OF_SEATS
import com.dinder.rihla.rider.data.model.Seat

enum class SeatState {
    UN_SELECTED,
    SELECTED,
    UNBOOKED,
    BOOKED
}

object SeatUtils {
    val emptySeats: Map<String, SeatState> = (1..NUMBER_OF_SEATS).map {
        Pair(
            it.toString(),
            SeatState.UN_SELECTED
        )
    }.toList().toMap()

    fun getSelectedSeats(seats: Map<String, SeatState>): List<Seat> =
        seats.filter { it.value == SeatState.SELECTED }
            .map { Seat(number = it.key.toInt(), status = it.value) }

    //    fun getSelectedSeatsAsUnbooked(seats: Map<String, SeatState>): List<Seat> {
//        val selected = getSelectedSeats(seats)
//        return selected.map {
//            Seat(number = it.toInt(), passenger = null, status = SeatState.UNBOOKED)
//        }
//    }
//
//    fun getTripSeatsCount(seats: List<Seat>) = seats.size.toString()
//
//    fun getTripReservedSeatsCount(seats: List<Seat>) =
//        seats.filter { it.status == SeatState.BOOKED }
//            .size.toString()
//
    // Convert List of [Seat] to SeatView state Map
    fun getSeatsListAsStateMap(seats: List<Seat>): Map<String, SeatState> = seats.map {
        it.number.toString() to it.status
    }.toMap()

    // Convert remote json Map to list of [Seat]
    fun seatsMapToModel(seats: Map<String, Map<String, Any?>>): List<Seat> = seats.map {
        Seat.fromJson(
            mapOf(
                it.key to mapOf(
                    "passenger" to it.value["passenger"] as String?,
                    "status" to SeatState.valueOf(it.value["status"].toString())
                )
            )
        )
    }.toList().sortedBy { it.number }

    // Convert list of [Seat] to map
    fun seatsModelToMap(seats: List<Seat>): Map<String, Map<String, Any?>> = seats.map {
        it.number.toString() to mapOf(
            "passenger" to it.passenger,
            "status" to it.status,
            "passengerPhoneNumber" to it.passengerPhoneNumber
        )
    }.toMap()
}
