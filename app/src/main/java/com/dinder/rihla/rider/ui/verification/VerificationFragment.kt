package com.dinder.rihla.rider.ui.verification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dinder.rihla.rider.common.Constants
import com.dinder.rihla.rider.common.RihlaFragment
import com.dinder.rihla.rider.databinding.VerificationFragmentBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class VerificationFragment : RihlaFragment() {

    private val viewModel: VerificationViewModel by viewModels()
    private val args: VerificationFragmentArgs by navArgs()
    private lateinit var binding: VerificationFragmentBinding
    private var verificationID: String = "ID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sendSmsCode(args.phoneNumber)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = VerificationFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        binding.verificationCode.addTextChangedListener { code ->
            code?.let {
                if (code.length == Constants.VERIFICATION_CODE_LENGTH) {
                    val credential =
                        PhoneAuthProvider.getCredential(verificationID, code.toString())
                    viewModel.onVerificationAttempt(args.phoneNumber, credential)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    binding.verificationProgressBar.isVisible = it.loading

                    it.messages.firstOrNull()?.let {
                        showSnackbar(it.content)
                        viewModel.userMessageShown(it.id)
                    }

                    if (it.navigateToHome) {
                        showSnackbar("Navigating to home")
                    }

                    if (it.navigateToSignup) {
                        navigateToSignup()
                    }
                }
            }
        }
    }

    private fun sendSmsCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity!!)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credentials: PhoneAuthCredential) {
                    viewModel.onVerificationAttempt(args.phoneNumber, credentials)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(verificationId, token)
                    verificationID = verificationId
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun navigateToSignup() {
        findNavController().navigate(
            VerificationFragmentDirections.actionVerificationFragmentToSignupFragment(
                args.phoneNumber
            )
        )
    }
}
