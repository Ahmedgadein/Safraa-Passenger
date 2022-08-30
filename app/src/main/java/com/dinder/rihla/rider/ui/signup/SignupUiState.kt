package com.dinder.rihla.rider.ui.signup

import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.data.model.User

data class SignupUiState(
    val loading: Boolean = false,
    val navigateToHome: Boolean = false,
    val user: User? = null,
    val messages: List<Message> = emptyList()
)
