package com.dinder.rihla.rider.ui.update

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dinder.rihla.rider.R
import com.dinder.rihla.rider.databinding.FragmentUpdateAppBinding

class UpdateAppFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentUpdateAppBinding = FragmentUpdateAppBinding.inflate(inflater, container, false)
        binding.updateAppButton.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.dinder.rihla.rider.release")
                )
            )
        }
        return binding.root
    }
}
