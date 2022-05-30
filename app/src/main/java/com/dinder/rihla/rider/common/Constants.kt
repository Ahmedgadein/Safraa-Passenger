package com.dinder.rihla.rider.common

object Constants {
    const val NUMBER_OF_SEATS_ROWS = 11
    const val NUMBER_OF_SEATS = 49
    const val VERIFICATION_CODE_LENGTH = 6
    const val DATABASE_NAME = "rihla_database"
    const val DAY_MILLISECONDS: Long = 86400000
    const val HOUR_MILLISECONDS = 3600000
    const val MINUTE_MILLISECONDS = 60000
}

object Collections {
    const val DESTINATIONS = "destinations"
    const val USERS = "users"
    const val TRIPS = "trips"
    const val TICKETS = "tickets"
}

object Fields {
    const val PHONE_NUMBER = "phoneNumber"
    const val PASSENGER_ID = "passengerId"
    const val SEATS = "seats"
    const val DATE = "date"
    const val ID = "id"
    const val FROM = "from"
    const val TO = "to"
}
