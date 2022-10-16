package com.dinder.rihla.rider.ui.settings

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.dinder.rihla.rider.R
import com.dinder.rihla.rider.data.model.Role
import com.dinder.rihla.rider.data.model.User
import com.dinder.rihla.rider.databinding.ContactOptionsBottomsheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
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
        displayPreferences(null)
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
                        displayPreferences(it)
                        setClickListeners()
                    }
                }
            }
        }
    }

    private fun setClickListeners() {
        findPreference<Preference>("language")?.setOnPreferenceClickListener {
            val currentLanguage = preferences.getString("language", "en")
            preferences.edit().apply {
                putString("language", if (currentLanguage == "ar") "en" else "ar")
                apply()
            }.commit()
            activity?.recreate()
            true
        }

        findPreference<SwitchPreference>("night_mode")?.setOnPreferenceChangeListener { preference, night ->
            val mode =
                if (night as Boolean) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            preferences.edit().apply {
                putBoolean("night_mode", night)
                apply()
            }.commit()
            AppCompatDelegate.setDefaultNightMode(mode)
            true
        }

        findPreference<Preference>("terms_and_conditions")?.setOnPreferenceClickListener {
            showSnackbar("Terms And Conditions")
            true
        }

        findPreference<Preference>("contact_us")?.setOnPreferenceClickListener {
            showContactBottomSheet()
            true
        }
    }

    private fun displayPreferences(user: User?) {
        findPreference<Preference>("name")?.summary =
            preferences.getString("name", "NA")

        findPreference<Preference>("phone")?.summary =
            preferences.getString("phone", "NA")

        findPreference<Preference>("language")?.summary =
            getLanguage(preferences.getString("language", "en"))

        if (user?.role == Role.PASSENGER) {
            findPreference<Preference>("points")?.apply {
                isVisible = true
                summary = user.points.toString()
            }
        }
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

    private fun showContactBottomSheet() {
        val dialogBinding =
            ContactOptionsBottomsheetDialogBinding.inflate(layoutInflater, null, false)

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)
        bottomSheetDialog.show()

        dialogBinding.whatsapp.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/249117427796")))
        }

        dialogBinding.phoneCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+249117427796"))
            startActivity(intent)
        }
    }
}
