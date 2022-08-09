package com.dinder.rihla.rider.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dinder.rihla.rider.common.Message
import com.dinder.rihla.rider.common.Result
import com.dinder.rihla.rider.data.model.User
import com.dinder.rihla.rider.data.remote.auth.AuthRepository
import com.dinder.rihla.rider.data.remote.user.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.mixpanel.android.mpmetrics.MixpanelAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository,
    private val auth: FirebaseAuth,
    private val mixpanel: MixpanelAPI
) : ViewModel() {
    private val _state = MutableStateFlow(SignupUiState())
    val state = _state.asStateFlow()

    fun signup(user: User) {
        val _user = user.copy(id = auth.currentUser?.uid!!)
        viewModelScope.launch {
            authRepo.register(_user).collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(loading = true) }
                    is Result.Error -> showUserMessage(result.message)
                    is Result.Success -> {
                        mixpanel.identify(auth.uid)
                        val props = JSONObject().apply {
                            put("Name", _user.name)
                            put("Phone Number", _user.phoneNumber)
                        }
                        mixpanel.people.set(props)
                        mixpanel.track("Signup Successful")
                        userRepo.add(_user)
                        _state.update { it.copy(navigateToHome = true) }
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
