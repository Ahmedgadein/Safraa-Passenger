package com.dinder.rihla.rider.ui.signup

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.dinder.rihla.rider.R
import com.dinder.rihla.rider.common.RihlaFragment
import com.dinder.rihla.rider.data.model.Role
import com.dinder.rihla.rider.data.model.User
import com.dinder.rihla.rider.databinding.SignupFragmentBinding
import com.dinder.rihla.rider.utils.NameValidator
import com.dinder.rihla.rider.utils.NetworkUtils
import com.mixpanel.android.mpmetrics.MixpanelAPI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SignupFragment : RihlaFragment() {
    private val viewModel: SignupViewModel by viewModels()
    private val args: SignupFragmentArgs by navArgs()
    private lateinit var binding: SignupFragmentBinding

    @Inject
    lateinit var mixpanel: MixpanelAPI

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = SignupFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        binding.selection = Role.PASSENGER
        binding.radioGroupPlus.check(R.id.travler_radio_button)
        setTermsAndConditions()

        binding.signupButton.setOnClickListener {
            if (!NetworkUtils.isNetworkConnected(requireContext())) {
                showSnackbar(resources.getString(R.string.no_network))
                return@setOnClickListener
            }
            val name = binding.signupNameContainer.editText?.text.toString()
            if (!validName(name)) {
                return@setOnClickListener
            }
            mixpanel.track("Signup Attempt")
            viewModel.signup(
                User(
                    phoneNumber = args.phoneNumber,
                    name = name,
                    role = getSelectedRole()
                )
            )
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.signupNameContainer.editText?.isEnabled = !state.loading
                    binding.signupProgressBar.isVisible = state.loading
                    setRadioButtons()
                    state.messages.firstOrNull()?.let { message ->
                        showSnackbar(message.content)
                        viewModel.userMessageShown(message.id)
                    }

                    if (state.navigateToHome) {
                        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
                        preferences.edit().apply {
                            putString("id", state.user?.id)
                            apply()
                        }
                        navigateToHome(state.user)
                    }
                }
            }
        }
    }

    private fun getSelectedRole(): Role = when (binding.radioGroupPlus.checkedRadioButtonId) {
        R.id.travler_radio_button -> Role.PASSENGER
        else -> Role.AGENT
    }

    private fun setRadioButtons() {
        binding.radioGroupPlus.setOnCheckedChangeListener { radioGroupPlus, id ->
            when (id) {
                R.id.travler_radio_button -> binding.selection = Role.PASSENGER
                else -> binding.selection = Role.AGENT
            }
        }

        binding.passengerContainer.setOnClickListener {
            binding.radioGroupPlus.check(R.id.travler_radio_button)
        }

        binding.agentContainer.setOnClickListener {
            binding.radioGroupPlus.check(R.id.agent_radio_button)
        }
    }

    private fun setTermsAndConditions() {
        val termsAndConditions =
            SpannableString(getString(R.string.terms_and_conditions))

        val onShowTermsAndConditions = object : ClickableSpan() {

            override fun updateDrawState(ds: TextPaint) {
                ds.color = resources.getColor(R.color.teal_700, context?.theme)
                ds.isUnderlineText = false
            }

            override fun onClick(widget: View) {
                showToast("Terms And Conditions")
                view?.invalidateOutline()
            }
        }

        if (Locale.getDefault().language.equals(Locale("ar").language)) {
            termsAndConditions.setSpan(
                onShowTermsAndConditions,
                19,
                35,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            termsAndConditions.setSpan(
                onShowTermsAndConditions,
                termsAndConditions.indexOf("terms"),
                termsAndConditions.indexOf("conditions") + "conditions".length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.termsAndConditions.apply {
            text = termsAndConditions
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
    }

    private fun validName(name: String): Boolean {
        val error = NameValidator.validate(name, requireContext())
        binding.signupNameContainer.helperText = error
        return error == null
    }

    private fun navigateToHome(user: User?) {
        user?.let {
            val direction = if (user.role == Role.PASSENGER)
                SignupFragmentDirections.actionSignupFragmentToHomeFragment()
            else SignupFragmentDirections.actionSignupFragmentToAgentHomeFragment()
            findNavController().navigate(direction)
        }
    }
}
