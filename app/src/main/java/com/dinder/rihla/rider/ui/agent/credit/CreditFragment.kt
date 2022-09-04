package com.dinder.rihla.rider.ui.agent.credit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dinder.rihla.rider.R
import com.dinder.rihla.rider.databinding.CreditFragmentBinding
import com.dinder.rihla.rider.ui.agent.balance.BalanceFragment
import com.dinder.rihla.rider.ui.agent.transactions.TransactionsFragment
import com.google.android.material.tabs.TabLayoutMediator

class CreditFragment : Fragment() {
    private lateinit var binding: CreditFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = CreditFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        val adapter = object : FragmentStateAdapter(this@CreditFragment) {
            override fun getItemCount() = 2

            override fun createFragment(position: Int): Fragment {
                return mapToFragment(position)
            }
        }

        binding.viewpager.adapter = adapter

        TabLayoutMediator(
            binding.tabLayout,
            binding.viewpager
        ) { tab, position ->
            tab.text = mapTabTitle(position)
        }.attach()
    }

    private fun mapTabTitle(position: Int): CharSequence {
        return when (position) {
            0 -> getString(R.string.current_balance)
            else -> getString(R.string.transactions)
        }
    }

    private fun mapToFragment(position: Int): Fragment {
        return when (position) {
            0 -> BalanceFragment()
            else -> TransactionsFragment()
        }
    }
}
