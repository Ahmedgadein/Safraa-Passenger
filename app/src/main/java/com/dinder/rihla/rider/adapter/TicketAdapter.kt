package com.dinder.rihla.rider.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dinder.rihla.rider.data.model.Ticket
import com.dinder.rihla.rider.databinding.TicketItemListBinding
import com.dinder.rihla.rider.ui.home.HomeFragmentDirections
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.json.JSONObject

class TicketAdapter : ListAdapter<Ticket, TicketAdapter.TicketHolder>(TicketDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketHolder {
        return TicketHolder(
            TicketItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TicketHolder, position: Int) {
        val ticket = getItem(position)
        return holder.bind(ticket)
    }

    class TicketHolder(private val binding: TicketItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var ticket: Ticket? = null
        private val mixpanel: MixpanelAPI =
            MixpanelAPI.getInstance(itemView.context, "244608d9170c936a37d24ef9a7b8eccf")

        init {
            binding.root.setOnClickListener {
                ticket?.let {
                    val props = JSONObject().apply {
                        put("Ticket ID", it.id)
                        put("Trip ID", it.trip.id)
                        put("Seats", it.seats.toString())
                        put("From", it.trip.from.name)
                        put("To", it.trip.to.name)
                        put("Date", it.trip.date)
                        put("Time", it.trip.time)
                    }
                    mixpanel.track("View Ticket", props)
                    binding.root.findNavController()
                        .navigate(
                            HomeFragmentDirections.actionHomeFragmentToTicketDetail(
                                ticketId = it.id!!
                            )
                        )
                }
            }
        }

        fun bind(ticket: Ticket) {
            this.ticket = ticket
            binding.ticket = ticket
        }
    }
}

class TicketDiffCallback : DiffUtil.ItemCallback<Ticket>() {
    override fun areItemsTheSame(oldItem: Ticket, newItem: Ticket): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Ticket, newItem: Ticket): Boolean {
        return oldItem.passengerName == newItem.passengerName
    }
}
