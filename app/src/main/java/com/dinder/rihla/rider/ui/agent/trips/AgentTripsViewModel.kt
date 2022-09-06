package com.dinder.rihla.rider.ui.agent.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.remote.user.UserRepository
import com.dinder.rihla.rider.domain.GetAgentTripsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AgentTripsViewModel @Inject constructor(
    private val useCase: GetAgentTripsUseCase,
    private val userRepo: UserRepository,
) :
    ViewModel() {
    private val _state = MutableStateFlow(AgentTripsUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            userRepo.user.collect { user ->
                user?.let {
                    _state.update { it.copy(user = user) }
                }
            }
            getTrips()
        }
    }

    private fun getTrips() {
        viewModelScope.launch {
            useCase.invoke().collect { result ->

                when (result) {
                    Result.Loading -> _state.update { it.copy(loading = true) }
                    is Result.Error -> showUserMessage(result.message)
                    is Result.Success -> _state.update {
                        it.copy(
                            trips = result.value,
                            loading = false
                        )
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
