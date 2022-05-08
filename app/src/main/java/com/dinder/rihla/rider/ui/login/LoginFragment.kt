package com.dinder.rihla.rider.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.dinder.rihla.rider.common.RihlaFragment
import com.dinder.rihla.rider.databinding.LoginFragmentBinding
import com.dinder.rihla.rider.utils.PhoneNumberFormatter
import com.dinder.rihla.rider.utils.PhoneNumberValidator

class LoginFragment : RihlaFragment() {
    lateinit var binding: LoginFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        binding.loginPhoneNumberContainer.editText?.addTextChangedListener {
            it?.let {
                binding.loginPhoneNumberContainer.helperText =
                    PhoneNumberValidator.validate(it.toString())
            }
        }

        binding.loginButton.setOnClickListener {
            val phoneNumber = binding.loginPhoneNumberContainer.editText?.text.toString()
            if (!validNumber(phoneNumber)) {
                return@setOnClickListener
            }
            navigateToVerification(PhoneNumberFormatter.getFullNumber(phoneNumber))
        }
    }

    private fun validNumber(number: String): Boolean {
        with(binding.loginPhoneNumberContainer) {
            helperText = PhoneNumberValidator.validate(number)
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
