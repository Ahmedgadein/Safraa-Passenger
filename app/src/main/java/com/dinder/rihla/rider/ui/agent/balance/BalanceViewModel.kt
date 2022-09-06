package com.dinder.rihla.rider.ui.agent.balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.remote.user.UserRepository
import com.dinder.rihla.rider.data.remote.wallet.WalletRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BalanceViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val walletRepo: WalletRepositoryImpl,
) : ViewModel() {

    private val _state = MutableStateFlow(BalanceUiState())
    val state = _state.asStateFlow()

    init {
        getWalletBalance()
    }

    fun getWalletBalance() {
        viewModelScope.launch {
            userRepo.user.collect { user ->
                user?.let {
                    walletRepo.getBalance(user.id).collect { result ->
                        when (result) {
                            Result.Loading -> _state.update { it.copy(loading = true) }
                            is Result.Error -> {
                                showUserMessage(result.message)
                                _state.update {
                                    it.copy(
                                        loading = false,
                                        error = true
                                    )
                                }
                            }
                            is Result.Success -> _state.update {
                                it.copy(
                                    loading = false,
                                    balance = result.value
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun retry() {
        _state.update { it.copy(loading = false, error = false, balance = null) }
        getWalletBalance()
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
