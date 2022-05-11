package com.dinder.rihla.rider.ui.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.remote.auth.AuthRepository
import com.dinder.rihla.rider.data.remote.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandingPageViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository
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
                            _state.update {
                                it.copy(
                                    navigateToHome = loggedIn.value && user != null,
                                    navigateToLogin = !loggedIn.value || user == null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
