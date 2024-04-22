/*
 * SPDX-FileCopyrightText: 2024 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package org.calyxos.datura.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dagger.hilt.android.AndroidEntryPoint
import lineageos.providers.LineageSettings
import org.calyxos.datura.R
import org.calyxos.datura.databinding.FragmentSettingsBinding
import org.calyxos.datura.service.DaturaService
import org.calyxos.datura.utils.CommonUtils.PREFERENCE_DEFAULT_INTERNET
import org.calyxos.datura.utils.CommonUtils.PREFERENCE_NOTIFICATIONS

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
                if (!newValue.toString().toBoolean()) {
                    requireContext().startForegroundService(Intent(context, DaturaService::class.java))
                } else {
                    requireContext().stopService(Intent(context, DaturaService::class.java))
                }
                LineageSettings.Secure.putInt(
                    context?.contentResolver,
                    LineageSettings.Secure.DEFAULT_RESTRICT_NETWORK_DATA,
                    if (newValue as Boolean) {
                        0
                    } else {
                        1
                    }
                )
            }
        }

        findPreference<Preference>(PREFERENCE_NOTIFICATIONS)?.apply {
            setOnPreferenceClickListener {
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).also {
                    it.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    startActivity(it)
                }
                true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
