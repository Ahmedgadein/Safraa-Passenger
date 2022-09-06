package com.dinder.rihla.rider.ui.agent.balance

import com.dinder.rihla.rider.common.Message

data class BalanceUiState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val balance: Double? = null,
    val messages: List<Message> = emptyList(),
)
