package com.dinder.rihla.rider.ui.landing

import com.dinder.rihla.rider.common.Message

data class LandingUiState(
    val navigateToHome: Boolean = false,
    val navigateToLogin: Boolean = false,
    val navigateToUpdate: Boolean = false,
    val messages: List<Message> = emptyList()
)
