package com.dinder.rihla.rider.ui.agent.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dinder.rihla.rider.R
import com.dinder.rihla.rider.databinding.AgentHomeFragmentBinding
import com.dinder.rihla.rider.ui.agent.credit.CreditFragment
import com.dinder.rihla.rider.ui.agent.trips.AgentTripsFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AgentHomeFragment : Fragment() {
    private val viewModel: AgentHomeViewModel by viewModels()
    private lateinit var binding: AgentHomeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = AgentHomeFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        binding.viewpager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> AgentTripsFragment()
                    else -> CreditFragment()
                }
            }
        }
        binding.viewpager.isUserInputEnabled = false

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.trips -> setFragment(0)
                R.id.credit -> setFragment(1)
            }
            true
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    if (!it.userIsActivated) {
                        navigateToAccountDeactivatedScreen()
                    }
                }
            }
        }
    }

    private fun setFragment(position: Int) {
        binding.viewpager.currentItem = position
    }

    private fun navigateToAccountDeactivatedScreen() {
        findNavController().navigate(
            AgentHomeFragmentDirections.actionAgentHomeFragmentToAccountDeactivatedFragment()
        )
    }
}
