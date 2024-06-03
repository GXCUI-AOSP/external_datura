/*
 * SPDX-FileCopyrightText: 2024 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package org.calyxos.datura.settings

import android.content.Intent
import android.os.Bundle
import android.os.UserManager
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dagger.hilt.android.AndroidEntryPoint
import lineageos.providers.LineageSettings
import org.calyxos.datura.R
import org.calyxos.datura.databinding.FragmentSettingsBinding
import org.calyxos.datura.utils.CommonUtils.PREFERENCE_DEFAULT_INTERNET

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)

        findPreference<SwitchPreferenceCompat>(PREFERENCE_DEFAULT_INTERNET)?.let {
            it.isChecked = LineageSettings.Secure.getInt(
                context?.contentResolver,
                LineageSettings.Secure.DEFAULT_RESTRICT_NETWORK_DATA,
                0
            ) == 0
            it.setOnPreferenceChangeListener { _, newValue ->
                val userManager = context?.getSystemService(UserManager::class.java)
                var result = true
                if (userManager != null) {
                    for (userHandle in userManager.userProfiles) {
                        result = result && LineageSettings.Secure.putIntForUser(
                            context?.contentResolver,
                            LineageSettings.Secure.DEFAULT_RESTRICT_NETWORK_DATA,
                            if (newValue as Boolean) {
                                0
                            } else {
                                1
                            },
                            userHandle.getIdentifier()
                        )
                    }
                }
                result
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
