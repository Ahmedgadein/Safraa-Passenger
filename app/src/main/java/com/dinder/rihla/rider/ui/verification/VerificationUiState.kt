package com.dinder.rihla.rider.ui.verification

import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.data.model.User

data class VerificationUiState(
    val loading: Boolean = false,
    val navigateToHome: Boolean = false,
    val navigateToSignup: Boolean = false,
    val user: User? = null,
    val messages: List<Message> = emptyList()
)
