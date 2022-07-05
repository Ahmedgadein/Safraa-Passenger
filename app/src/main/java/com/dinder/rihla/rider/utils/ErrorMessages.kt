package com.dinder.rihla.rider.utils

import android.content.res.Resources
import com.dinder.rihla.rider.R
import javax.inject.Inject

class ErrorMessages @Inject constructor(private val resources: Resources) {
    private fun getResource(id: Int): String = resources.getString(id)

    val failedToResolveRegistration: String = getResource(R.string.failed_to_resolve_registration)

    val signupFailed: String = getResource(R.string.signup_failed)

    val loginFailed: String = getResource(R.string.login_failed)

    val loadingDestinationsFailed: String = getResource(R.string.loading_destinations_failed)

    val loadingTicketsFailed: String = getResource(R.string.loading_tickets_failed)

    val ticketNotFound: String = getResource(R.string.ticket_not_found)

    val failedToSaveTicket: String = getResource(R.string.failed_to_save_ticket)

    val loadingTripsFailed: String = getResource(R.string.loading_trips_failed)

    val tripNotFound: String = getResource(R.string.trip_not_found)

    val failedToLoadTrip: String = getResource(R.string.failed_to_load_trip)

    val failedToReserveSeat: String = getResource(R.string.failed_to_reserve_seat)

    val couldntFindUser: String = getResource(R.string.could_not_find_user)
}
