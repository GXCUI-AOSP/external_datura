/*
 * SPDX-FileCopyrightText: 2024 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package org.calyxos.datura.settings

import android.os.Bundle
import android.os.StrictMode
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import org.calyxos.datura.R

import lineageos.providers.LineageSettings

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)

        findPreference<SwitchPreferenceCompat>("PREFERENCE_DEFAULT_INTERNET")?.let {
            it.setOnPreferenceClickListener {
                // TODO: DO SOMETHING HERE!
                true
            }
        }

        findPreference<SwitchPreferenceCompat>("cleartext_network_policy")?.let {
            it.isChecked = LineageSettings.Global.getInt(
                context?.contentResolver,
                LineageSettings.Global.CLEARTEXT_NETWORK_POLICY,
                StrictMode.NETWORK_POLICY_INVALID
            ) == StrictMode.NETWORK_POLICY_REJECT
            it.setOnPreferenceChangeListener { _, newValue ->
                LineageSettings.Global.putInt(
                    context?.contentResolver,
                    LineageSettings.Global.CLEARTEXT_NETWORK_POLICY,
                    if (newValue as Boolean) StrictMode.NETWORK_POLICY_REJECT
                    else StrictMode.NETWORK_POLICY_INVALID
                )
            }
        }
    }
}