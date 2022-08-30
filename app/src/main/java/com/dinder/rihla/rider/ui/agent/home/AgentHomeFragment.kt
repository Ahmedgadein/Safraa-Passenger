package com.dinder.rihla.rider.ui.agent.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dinder.rihla.rider.R

class AgentHomeFragment : Fragment() {

    companion object {
        fun newInstance() = AgentHomeFragment()
    }

    private lateinit var viewModel: AgentHomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.agent_home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AgentHomeViewModel::class.java)
        // TODO: Use the ViewModel
    }
}
