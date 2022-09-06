package com.dinder.rihla.rider.data.model

import com.dinder.rihla.rider.utils.DateTimeUtils
import java.util.Date
import kotlin.time.ExperimentalTime

data class Transaction(
    val time: Date,
    val type: TransactionType,
    val amount: Double,
    val accountNumber: String? = null,
    val accountName: String? = null,
    val seatCommission: Double? = null,
    val seatCount: Int? = null,
) {
    @OptIn(ExperimentalTime::class)
    companion object {
        fun fromJson(json: Map<String, Any>): Transaction {
            return Transaction(
                time = DateTimeUtils.decodeTimeStamp(json["time"]),
                type = TransactionType.valueOf(json["type"].toString()),
                amount = json["amount"].toString().toDouble(),
                accountNumber = json["accountNumber"] as String?,
                accountName = json["accountName"] as String?,
                seatCommission = if (json["seatCommission"] != null) if (json["seatCommission"] is Long) (json["seatCommission"] as Long).toDouble() else (json["seatCommission"] as Double) else null,
                seatCount = (json["seatCount"] as Long?)?.toInt()
            )
        }
    }
}

enum class TransactionType {
    WITHDRAW,
    EARNING
}
