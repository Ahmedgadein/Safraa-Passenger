package com.dinder.rihla.rider.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.remote.user.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: UserRepository,
    private val auth: FirebaseAuth,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsUiState())
    val state = _state.asStateFlow()

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch {
            repository.get(auth.currentUser?.uid!!).collect { result ->
                when (result) {
                    Result.Loading -> Unit
                    is Result.Error -> showUserMessage(result.message)
                    is Result.Success -> _state.update { it.copy(user = result.value) }
                }
            }
        }
    }

    fun logout() {
        auth.signOut()
    }

    private fun showUserMessage(content: String) {
        _state.update { state ->
            val messages = state.messages + Message(
                id = UUID.randomUUID().mostSignificantBits,
                content = content
            )
            state.copy(messages = messages)
        }
    }

    fun userMessageShown(messageId: Long) {
        _state.update { state ->
            val messages = state.messages.filterNot { it.id == messageId }
            state.copy(messages = messages)
        }
    }
}
