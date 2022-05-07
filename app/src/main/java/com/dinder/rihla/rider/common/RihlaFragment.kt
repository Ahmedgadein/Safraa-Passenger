package com.dinder.rihla.rider.common

import android.widget.Toast
import androidx.fragment.app.Fragment

open class RihlaFragment : Fragment() {
    protected fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
