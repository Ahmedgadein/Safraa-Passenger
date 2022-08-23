package com.dinder.rihla.rider.ui.trip_detail // ktlint-disable experimental:package-name

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Seat
import com.dinder.rihla.rider.data.remote.trip.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val repository: TripRepository
) :
    ViewModel() {

    private val _state = MutableStateFlow(TripDetailUiState())
    val state = _state.asStateFlow()

    fun getTrip(id: String) {
        observeTrip(id)
    }

    private fun observeTrip(id: String) {
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

    fun reserveSeats(tripId: String, seats: List<Seat>) {
        val seats = seats.map { it.number.toString() }
        viewModelScope.launch {
            repository.reserveSeats(tripId, seats).collect { result ->
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
