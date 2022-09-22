package com.dinder.rihla.rider.ui.agent.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dinder.rihla.rider.adapter.TransactionAdapter
import com.dinder.rihla.rider.common.RihlaFragment
import com.dinder.rihla.rider.databinding.TransactionsFragmentBinding
import com.mixpanel.android.mpmetrics.MixpanelAPI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TransactionsFragment : RihlaFragment() {
    private val viewModel: TransactionsViewModel by viewModels()
    private lateinit var binding: TransactionsFragmentBinding

    @Inject
    lateinit var mixpanel: MixpanelAPI

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = TransactionsFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mixpanel.track("Agent View Transactions")
    }

    private fun setUI() {
        val transactionAdapter = TransactionAdapter()
        binding.recyclerView.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(context)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    state.messages.firstOrNull()?.let {
                        showSnackbar(it.content)
                        viewModel.userMessageShown(it.id)
                    }

                    if (state.loading) {
                        binding.loading.isVisible = true
                        binding.error.isVisible = false
                        return@collect
                    }

                    if (state.error) {
                        binding.loading.isVisible = false
                        binding.error.isVisible = true
                        return@collect
                    }

                    if (!state.loading && !state.error) {

                        binding.loading.isVisible = false
                        binding.error.isVisible = false
                        transactionAdapter.submitList(state.transactions)
                    }
                }
            }
        }
    }
}
