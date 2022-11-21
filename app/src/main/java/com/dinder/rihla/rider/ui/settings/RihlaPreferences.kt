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
import com.dinder.rihla.rider.databinding.SocialMediaBottomsheetDialogBinding
import com.dinder.rihla.rider.utils.DynamicLinkUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.dynamiclinks.ktx.socialMetaTagParameters
import com.google.firebase.ktx.Firebase
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
                        setClickListeners(it)
                    }
                }
            }
        }
    }

    private fun setClickListeners(user: User) {
        findPreference<Preference>("language")?.setOnPreferenceClickListener {
            val currentLanguage = preferences.getString("language", "ar")
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
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://ahmedgadein0.wixsite.com/safraa-terms-and-con")
                )
            )
            true
        }

        findPreference<Preference>("contact_us")?.setOnPreferenceClickListener {
            showContactBottomSheet()
            true
        }

        findPreference<Preference>("social_media")?.setOnPreferenceClickListener {
            showSocialMediaBottomSheet()
            true
        }

        findPreference<Preference>("invite")?.setOnPreferenceClickListener {
            val invitationLink = DynamicLinkUtils.invitationLink(user.id)
            Firebase.dynamicLinks.shortLinkAsync {
                link = invitationLink
                domainUriPrefix = DynamicLinkUtils.getAppPrefix()
                androidParameters("com.dinder.rihla.rider.release") {
                    minimumVersion = 1005
                }
                socialMetaTagParameters {
                    title = getString(R.string.join_safraa)
                    description = getString(R.string.join_safraa_detail)
                    imageUrl = Uri.parse(getString(R.string.invite_link_image_url))
                }
            }.addOnSuccessListener { shortDynamicLink ->
                val mInvitationUrl = shortDynamicLink.shortLink

                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.invitation_link, mInvitationUrl))
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(sendIntent, getString(R.string.invite_a_friend))
                startActivity(shareIntent)
            }
            true
        }
    }

    private fun displayPreferences(user: User?) {
        findPreference<Preference>("name")?.summary =
            preferences.getString("name", "NA")

        findPreference<Preference>("phone")?.summary =
            preferences.getString("phone", "NA")

        findPreference<Preference>("language")?.summary =
            getLanguage(preferences.getString("language", "ar"))

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

    private fun showSocialMediaBottomSheet() {
        val dialogBinding =
            SocialMediaBottomsheetDialogBinding.inflate(layoutInflater, null, false)

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)
        bottomSheetDialog.show()

        dialogBinding.facebook.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/profile.php?id=100084529210329")
                )
            )
        }

        dialogBinding.instagram.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://instagram.com/safraa_sd?igshid=YmMyMTA2M2Y=")
                )
            )
        }

        dialogBinding.twitter.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/Safraa_sd?t=ldRpeyVmN6vQBQNvdHVCuA&s=09")
                )
            )
        }
    }
}
