package com.dinder.rihla.rider.ui.verification

import com.dinder.rihla.rider.common.Message

data class VerificationUiState(
    val loading: Boolean = false,
    val navigateToHome: Boolean = false,
    val navigateToSignup: Boolean = false,
    val messages: List<Message> = emptyList()
)
