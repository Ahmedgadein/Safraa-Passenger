package com.dinder.rihla.rider.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dinder.rihla.rider.data.model.Trip
import com.dinder.rihla.rider.databinding.TripItemListBinding
import com.dinder.rihla.rider.ui.home.HomeFragmentDirections
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.json.JSONObject

class TripAdapter() : ListAdapter<Trip, TripAdapter.TripHolder>(TripDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripHolder {
        return TripHolder(
            TripItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TripHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class TripHolder(private val binding: TripItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var trip: Trip? = null
        private val mixpanel: MixpanelAPI =
            MixpanelAPI.getInstance(itemView.context, "244608d9170c936a37d24ef9a7b8eccf")

        init {
            binding.root.setOnClickListener {
                trip?.let {
                    val props = JSONObject().apply {
                        put("Trip ID", it.id)
                        put("Seats", it.seats.toString())
                        put("From", it.from.name)
                        put("To", it.to.name)
                        put("Departure", it.departure)
                        put("Price", it.price)
                    }
                    mixpanel.track("View trip", props)
                    binding.root.findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToTripDetailFragment(
                            tripID = it.id!!
                        )
                    )
                }
            }
        }

        fun bind(trip: Trip) {
            this.trip = trip
            binding.trip = trip
        }
    }
}

class TripDiffCallback : DiffUtil.ItemCallback<Trip>() {
    override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean {
        return oldItem.seats == newItem.seats
    }
}
