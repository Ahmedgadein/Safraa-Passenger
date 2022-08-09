package com.dinder.rihla.rider.ui.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.remote.auth.AuthRepository
import com.dinder.rihla.rider.data.remote.user.UserRepository
import com.dinder.rihla.rider.domain.UpdateAppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LandingPageViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository,
    private val updateAppUseCase: UpdateAppUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(LandingUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            authRepo.isLoggedIn().collect { loggedIn ->
                when (loggedIn) {
                    Result.Loading -> Unit
                    is Result.Error -> Unit
                    is Result.Success -> {
                        userRepo.user.collect { user ->
                            updateAppUseCase().collect { shouldUpdate ->
                                when (shouldUpdate) {
                                    Result.Loading -> Unit
                                    is Result.Error -> showUserMessage(shouldUpdate.message)
                                    is Result.Success -> {
                                        _state.update { state ->
                                            state.copy(
                                                navigateToUpdate = shouldUpdate.value,
                                                navigateToHome = loggedIn.value && user != null &&
                                                    !shouldUpdate.value,
                                                navigateToLogin = !loggedIn.value || user == null &&
                                                    !shouldUpdate.value
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
    }

    private fun showUserMessage(content: String) {
        _state.update {
            val messages = it.messages + Message(UUID.randomUUID().mostSignificantBits, content)
            it.copy(messages = messages)
        }
    }

    fun userMessageShown(messageId: Long) {
        _state.update { state ->
            val messages = state.messages.filterNot { it.id == messageId }
            state.copy(messages = messages)
        }
    }
}
