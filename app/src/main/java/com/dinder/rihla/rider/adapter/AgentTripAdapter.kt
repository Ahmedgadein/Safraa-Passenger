package com.dinder.rihla.rider.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dinder.rihla.rider.data.model.Trip
import com.dinder.rihla.rider.databinding.AgentTripItemListBinding

class AgentTripAdapter() : ListAdapter<Trip, AgentTripAdapter.TripHolder>(TripDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripHolder {
        return TripHolder(
            AgentTripItemListBinding.inflate(
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

    class TripHolder(private val binding: AgentTripItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: Trip) {
            binding.trip = trip
        }
    }
}
