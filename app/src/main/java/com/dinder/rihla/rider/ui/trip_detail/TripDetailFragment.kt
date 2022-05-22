package com.dinder.rihla.rider.ui.trip_detail // ktlint-disable experimental:package-name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dinder.rihla.rider.R

class TripDetailFragment : Fragment() {

    companion object {
        fun newInstance() = TripDetailFragment()
    }

    private lateinit var viewModel: TripDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.trip_detail_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TripDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }
}
