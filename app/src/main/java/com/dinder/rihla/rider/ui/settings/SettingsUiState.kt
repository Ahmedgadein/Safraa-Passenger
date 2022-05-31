package com.dinder.rihla.rider.ui.settings

import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.data.model.User

data class SettingsUiState(
    val messages: List<Message> = emptyList(),
    val user: User? = null
)
