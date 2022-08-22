package com.dinder.rihla.rider.services

import com.google.firebase.messaging.FirebaseMessagingService

class RihlaFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}
