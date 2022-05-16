package com.dinder.rihla.rider.ui.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.remote.auth.AuthRepository
import com.dinder.rihla.rider.data.remote.user.UserRepository
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository
) :
    ViewModel() {
    private val _state: MutableStateFlow<VerificationUiState> =
        MutableStateFlow(VerificationUiState())
    val state: StateFlow<VerificationUiState> = _state.asStateFlow()

    fun onVerificationAttempt(phoneNumber: String, credential: AuthCredential) {
        viewModelScope.launch {
            authRepo.isRegistered(phoneNumber).collect { registered ->
                when (registered) {
                    Result.Loading -> _state.update { it.copy(loading = true) }
                    is Result.Error -> showUserMessage(registered.message)
                    is Result.Success -> {
                        userRepo.user.collect { user ->
                            authRepo.login(credential).collect { login ->
                                when (login) {
                                    Result.Loading -> Unit
                                    is Result.Error -> showUserMessage(login.message)
                                    is Result.Success -> _state.update {
                                        it.copy(
                                            loading = false,
                                            navigateToHome = (registered.value && user != null) && login.value,
                                            navigateToSignup = (!registered.value || user == null) && login.value
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showUserMessage(content: String) {
        _state.update { state ->
            val messages = state.messages + Message(
                id = UUID.randomUUID().mostSignificantBits,
                content = content
            )
            state.copy(messages = messages, loading = false)
        }
    }

    fun userMessageShown(messageId: Long) {
        _state.update { state ->
            val messages = state.messages.filterNot { it.id == messageId }
            state.copy(messages = messages)
        }
    }
}
