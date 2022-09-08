package com.dinder.rihla.rider.ui.agent.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.remote.user.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AgentHomeViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(AgentHomeUiState())
    val state = _state.asStateFlow()

    init {
        checkUserActivation()
    }

    private fun checkUserActivation() {
        viewModelScope.launch {
            userRepository.get(auth.currentUser?.uid!!).collect { result ->
                when (result) {
                    Result.Loading -> Unit
                    is Result.Error -> Unit
                    is Result.Success -> _state.update { it.copy(userIsActivated = result.value.active) }
                }
            }
        }
    }
}
