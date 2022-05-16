package com.dinder.rihla.rider.ui.home.tickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dinder.rihla.rider.R

class TicketsFragment : Fragment() {

    companion object {
        fun newInstance() = TicketsFragment()
    }

    private lateinit var viewModel: TicketsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tickets_fragment, container, false)
    }
}
