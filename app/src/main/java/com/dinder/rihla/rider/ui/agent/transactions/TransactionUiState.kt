package com.dinder.rihla.rider.ui.agent.transactions

import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.data.model.Transaction

data class TransactionUiState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val transactions: List<Transaction> = emptyList(),
    val messages: List<Message> = emptyList(),
)
