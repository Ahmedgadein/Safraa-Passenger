package com.dinder.rihla.rider.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dinder.rihla.rider.R
import com.dinder.rihla.rider.common.RihlaFragment
import com.dinder.rihla.rider.databinding.HomeFragmentBinding
import com.dinder.rihla.rider.ui.home.tickets.TicketsFragment
import com.dinder.rihla.rider.ui.home.trips.TripsFragment

class HomeFragment : RihlaFragment() {
    private lateinit var binding: HomeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        binding.viewpager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> TripsFragment()
                    else -> TicketsFragment()
                }
            }
        }
        binding.viewpager.isUserInputEnabled = false

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.trips -> setFragment(0)
                R.id.tickets -> setFragment(1)
            }
            true
        }
    }

    private fun setFragment(position: Int) {
        binding.viewpager.currentItem = position
    }
}
