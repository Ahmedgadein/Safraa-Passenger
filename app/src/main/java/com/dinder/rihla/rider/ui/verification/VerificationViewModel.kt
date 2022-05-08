package com.dinder.rihla.rider.ui.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.remote.auth.AuthRepository
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(private val repository: AuthRepository) :
    ViewModel() {
    private val _state: MutableStateFlow<VerificationUiState> =
        MutableStateFlow(VerificationUiState())
    val state: StateFlow<VerificationUiState> = _state.asStateFlow()

    fun onVerificationAttempt(phoneNumber: String, credential: AuthCredential) {
        viewModelScope.launch {
            repository.isRegistered(phoneNumber).collect { registered ->
                when (registered) {
                    Result.Loading -> _state.update { it.copy(loading = true) }
                    is Result.Error -> showUserMessage(registered.message)
                    is Result.Success -> {
                        repository.login(credential).collect { login ->
                            when (login) {
                                Result.Loading -> Unit
                                is Result.Error -> showUserMessage(login.message)
                                is Result.Success -> _state.update {
                                    it.copy(
                                        loading = false,
                                        navigateToHome = registered.value && login.value,
                                        navigateToSignup = !registered.value && login.value
                                    )
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
