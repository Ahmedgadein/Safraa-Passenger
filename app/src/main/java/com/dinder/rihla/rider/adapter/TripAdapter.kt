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

        init {
            binding.root.setOnClickListener {
                trip?.let {
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
