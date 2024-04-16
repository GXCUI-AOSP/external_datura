/*
 * SPDX-FileCopyrightText: 2024 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package org.calyxos.datura.settings

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import org.calyxos.datura.R
import org.calyxos.datura.databinding.FragmentSettingsBinding

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
