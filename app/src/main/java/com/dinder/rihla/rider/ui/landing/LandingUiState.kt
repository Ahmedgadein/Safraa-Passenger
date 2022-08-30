package com.dinder.rihla.rider.ui.landing

import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.data.model.User

data class LandingUiState(
    val navigateToHome: Boolean = false,
    val navigateToLogin: Boolean = false,
    val navigateToUpdate: Boolean = false,
    val user: User? = null,
    val messages: List<Message> = emptyList(),
)
