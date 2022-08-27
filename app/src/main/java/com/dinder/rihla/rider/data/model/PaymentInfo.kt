package com.dinder.rihla.rider.data.model

data class PaymentInfo(
    val ticket: Ticket,
    val accountNumber: String,
    val accountName: String,
    val billingWhatsappNumber: String,
) {
    companion object {
        fun fromJson(json: Map<String, Any>): PaymentInfo {
            return PaymentInfo(
                ticket = Ticket.fromJson(json["ticket"] as Map<String, Any>),
                accountNumber = json["accountNumber"] as String,
                accountName = json["accountName"] as String,
                billingWhatsappNumber = json["billingWhatsappNumber"] as String
            )
        }
    }
}
