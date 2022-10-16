package com.dinder.rihla.rider.ui.home.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.Destination
import com.dinder.rihla.rider.data.remote.destination.DestinationRepository
import com.dinder.rihla.rider.data.remote.rates.RateRepository
import com.dinder.rihla.rider.data.remote.trip.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val tripRepo: TripRepository,
    private val destinationRepo: DestinationRepository,
    private val rateRepo: RateRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(TripsUiState())
    val state = _state.asStateFlow()

    init {
        getTrips()
        getDestinations()
    }

    private fun getTrips() {
        viewModelScope.launch {
            rateRepo.observeRates().collect { rates ->
                when (rates) {
                    Result.Loading -> Unit
                    is Result.Error -> showUserMessage(rates.message)
                    is Result.Success -> {
                        queryTrips()
                    }
                }
            }
        }
    }

    private fun getDestinations() {
        viewModelScope.launch {
            destinationRepo.getDestinations().collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(loading = true) }
                    is Result.Error -> showUserMessage(result.message)
                    is Result.Success -> {
                        _state.update { it.copy(loading = false, destinations = result.value) }
                    }
                }
            }
        }
    }

    fun onDestinationUpdate(from: Destination? = null, to: Destination? = null) {
        from?.let { destination ->
            _state.update {
                it.copy(fromDestination = destination)
            }
        }
        to?.let { destination ->
            _state.update {
                it.copy(toDestination = destination)
            }
        }

        queryTrips()
    }

    fun clearDestinations() {
        _state.update {
            it.copy(toDestination = null, fromDestination = null)
        }
        queryTrips()
    }

    private fun queryTrips() {
        viewModelScope.launch {
            tripRepo.queryTrips(
                from = _state.value.fromDestination,
                to = _state.value.toDestination
            ).collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(loading = true) }
                    is Result.Error -> showUserMessage(result.message)
                    is Result.Success ->
                        rateRepo.getRates().collect { rates ->
                            when (rates) {
                                Result.Loading -> Unit
                                is Result.Error -> showUserMessage(rates.message)
                                is Result.Success -> _state.update {
                                    it.copy(
                                        loading = false,
                                        trips = result.value.map { it.copy(rate = rates.value.passenger) }
                                    )
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
