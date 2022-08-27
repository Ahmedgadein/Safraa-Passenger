package com.dinder.rihla.rider.ui.payment

import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.data.model.PaymentInfo

data class PaymentUiState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val paid: Boolean = false,
    val codeRedeemSuccessful: Boolean = false,
    val paymentInfo: PaymentInfo? = null,
    val messages: List<Message> = emptyList()
)
