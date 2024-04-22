/*
 * SPDX-FileCopyrightText: 2024 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package org.calyxos.datura.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dagger.hilt.android.AndroidEntryPoint
import org.calyxos.datura.R
import org.calyxos.datura.service.DaturaService
import org.calyxos.datura.utils.CommonUtils.PREFERENCE_DEFAULT_INTERNET
import org.calyxos.datura.utils.CommonUtils.PREFERENCE_NOTIFICATIONS

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)

        findPreference<SwitchPreferenceCompat>(PREFERENCE_DEFAULT_INTERNET)?.let {
            it.setOnPreferenceChangeListener { _, newValue ->
                if (!newValue.toString().toBoolean()) {
                    requireContext().startService(Intent(context, DaturaService::class.java))
                } else {
                    requireContext().stopService(Intent(context, DaturaService::class.java))
                }
                true
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
}
