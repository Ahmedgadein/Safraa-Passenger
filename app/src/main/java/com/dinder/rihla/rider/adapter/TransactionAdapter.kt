@file:OptIn(ExperimentalTime::class)

package com.dinder.rihla.rider.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dinder.rihla.rider.R
import com.dinder.rihla.rider.data.model.Transaction
import com.dinder.rihla.rider.data.model.TransactionType
import com.dinder.rihla.rider.databinding.TransactionItemListBinding
import com.dinder.rihla.rider.utils.DateTimeUtils
import kotlin.time.ExperimentalTime

class TransactionAdapter : ListAdapter<Transaction, TransactionAdapter.TransactionHolder>(
    TransactionDiffCallBack
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        return TransactionHolder(
            TransactionItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class TransactionHolder(private val binding: TransactionItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            // Time
            val timestamp = "${DateTimeUtils.getFormattedDate(transaction.time)} - ${
            DateTimeUtils.getFormattedTime(transaction.time)
            }"
            binding.time.text = timestamp

            // Type
            binding.type.text =
                itemView.resources.getString(if (transaction.type == TransactionType.WITHDRAW) R.string.withdraw else R.string.booked_ticket)

            when (transaction.type) {
                TransactionType.WITHDRAW -> {
                    // Amount
                    binding.amount.text = "- ${transaction.amount}"
                    binding.amount.setTextColor(
                        itemView.resources.getColor(
                            android.R.color.holo_red_light,
                            itemView.context.theme
                        )
                    )

                    // Account Number
                    binding.accountNumber.isVisible = true
                    binding.number.text = transaction.accountNumber

                    // Account Name
                    binding.accountName.isVisible = true
                    binding.name.text = transaction.accountName
                }
                TransactionType.EARNING -> {
                    // Amount
                    binding.amount.text = "+ ${transaction.amount}"
                    binding.amount.setTextColor(
                        itemView.resources.getColor(
                            R.color.green,
                            itemView.context.theme
                        )
                    )

                    // Count X Commission
                    binding.countCrossPrice.isVisible = true
                    binding.countCrossPrice.text =
                        itemView.resources.getString(
                            R.string.seat_times_price,
                            transaction.seatCount.toString(),
                            transaction.seatCommission.toString()
                        )
                }
            }
        }
    }
}

val TransactionDiffCallBack = object : DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem.type == newItem.type && oldItem.amount == newItem.amount
    }
}
