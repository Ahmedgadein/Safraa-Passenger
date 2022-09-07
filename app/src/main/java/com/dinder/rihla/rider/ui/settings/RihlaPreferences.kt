package com.dinder.rihla.rider.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.dinder.rihla.rider.R
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RihlaPreferences : PreferenceFragmentCompat() {
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences =
            PreferenceManager.getDefaultSharedPreferences(activity!!.baseContext)
        displayPreferences()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        setUI()
    }

    private fun setUI() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getUser()
                viewModel.state.collect { state ->
                    state.messages.firstOrNull()?.let {
                        showSnackbar(it.content)
                        viewModel.userMessageShown(it.id)
                    }
                    state.user?.let {
                        with(preferences.edit()) {
                            putString("name", it.name)
                            putString("phone", it.phoneNumber)
                            apply()
                        }
                        displayPreferences()
                        setClickListeners()
                    }
                }
            }
        }
    }

    private fun setClickListeners() {
        findPreference<Preference>("language")?.setOnPreferenceClickListener {
            val currentLanguage = preferences.getString("language", "ar")
            preferences.edit().apply {
                putString("language", if (currentLanguage == "ar") "en" else "ar")
            }.commit()
            activity?.recreate()
            true
        }
    }

    private fun displayPreferences() {
        findPreference<Preference>("name")?.summary =
            preferences.getString("name", "NA")

        findPreference<Preference>("phone")?.summary =
            preferences.getString("phone", "NA")

        findPreference<Preference>("language")?.summary =
            getLanguage(preferences.getString("language", "ar"))
    }

    private fun getLanguage(language: String?): String {
        return when (language) {
            "ar" -> "عربي"
            else -> "English"
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(view!!, message, Snackbar.LENGTH_SHORT)
            .show()
    }
}
