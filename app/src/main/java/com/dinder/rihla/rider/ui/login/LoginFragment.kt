package com.dinder.rihla.rider.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.dinder.rihla.rider.R
import com.dinder.rihla.rider.common.RihlaFragment
import com.dinder.rihla.rider.databinding.LoginFragmentBinding
import com.dinder.rihla.rider.utils.NetworkUtils
import com.dinder.rihla.rider.utils.PhoneNumberFormatter
import com.dinder.rihla.rider.utils.PhoneNumberValidator
import com.mixpanel.android.mpmetrics.MixpanelAPI
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : RihlaFragment() {
    lateinit var binding: LoginFragmentBinding

    @Inject
    lateinit var mixpanel: MixpanelAPI

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        binding.loginPhoneNumberContainer.editText?.addTextChangedListener {
            it?.let {
                binding.loginPhoneNumberContainer.helperText =
                    PhoneNumberValidator.validate(it.toString(), requireContext())
            }
        }

        binding.loginButton.setOnClickListener {
            if (!NetworkUtils.isNetworkConnected(requireContext())) {
                showSnackbar(resources.getString(R.string.no_network))
                return@setOnClickListener
            }
            val phoneNumber = binding.loginPhoneNumberContainer.editText?.text.toString()
            if (!validNumber(phoneNumber)) {
                return@setOnClickListener
            }
            mixpanel.track("Login Attempt")
            navigateToVerification(PhoneNumberFormatter.getFullNumber(phoneNumber))
        }

        val preferences =
            PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)
        val language = preferences.getString("language", "ar")

        setBackground(binding.english, language == "en")
        setBackground(binding.arabic, language == "ar")

        binding.english.setOnClickListener {
            preferences.edit().apply {
                putString("language", "en")
                apply()
            }.commit()
            activity?.recreate()
        }

        binding.arabic.setOnClickListener {
            preferences.edit().apply {
                putString("language", "ar")
                apply()
            }.commit()
            activity?.recreate()
        }
    }

    private fun validNumber(number: String): Boolean {
        with(binding.loginPhoneNumberContainer) {
            helperText = PhoneNumberValidator.validate(number, requireContext())
            return helperText == null
        }
    }

    private fun navigateToVerification(phoneNumber: String) {
        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToVerificationFragment(
                phoneNumber
            )
        )
    }
}

fun setBackground(view: View, selected: Boolean) {
    val resID = if (selected) R.drawable.radio_button_selected else R.drawable.radio_button_normal
    view.background = ResourcesCompat.getDrawable(view.context.resources, resID, view.context.theme)
}
