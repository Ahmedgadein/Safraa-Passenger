package com.dinder.rihla.rider.ui.trip_detail // ktlint-disable experimental:package-name

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Seat
import com.dinder.rihla.rider.data.remote.trip.TripRepository
import com.dinder.rihla.rider.domain.ReserveSeatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val repository: TripRepository,
    private val reserveSeatsUseCase: ReserveSeatsUseCase
) :
    ViewModel() {

    private val _state = MutableStateFlow(TripDetailUiState())
    val state = _state.asStateFlow()

    fun getTrip(id: Long) {
        observeTrip(id)
    }

    private fun observeTrip(id: Long) {
        viewModelScope.launch {
            repository.observeTrip(id).collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(loading = true) }
                    is Result.Error -> showUserMessage(result.message)
                    is Result.Success -> _state.update {
                        it.copy(
                            trip = result.value,
                            loading = false
                        )
                    }
                }
            }
        }
    }

    fun reserveSeats(tripId: Long, seats: List<Seat>) {
        viewModelScope.launch {
            reserveSeatsUseCase.invoke(tripId, seats).collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(loading = true) }
                    is Result.Error -> showUserMessage(result.message)
                    is Result.Success -> _state.update {
                        it.copy(
                            loading = false,
                            isReserved = true
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
