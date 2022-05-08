package com.dinder.rihla.rider.common

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

open class RihlaFragment : Fragment() {
    protected fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    protected fun showSnackbar(message: String) {
        Snackbar.make(view!!, message, Snackbar.LENGTH_LONG).show()
    }
}
