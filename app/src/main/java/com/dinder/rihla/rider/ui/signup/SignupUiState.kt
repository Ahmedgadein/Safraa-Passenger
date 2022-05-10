package com.dinder.rihla.rider.ui.signup

import com.dinder.rihla.rider.common.Message

data class SignupUiState(
    val loading: Boolean = false,
    val navigateToHome: Boolean = false,
    val messages: List<Message> = emptyList()
)
