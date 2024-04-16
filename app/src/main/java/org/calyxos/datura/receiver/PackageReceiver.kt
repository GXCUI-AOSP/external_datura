/*
 * SPDX-FileCopyrightText: 2024 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package org.calyxos.datura.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkPolicyManager
import android.os.Process.INVALID_UID
import androidx.preference.PreferenceManager

class PackageReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val uid = intent.getIntExtra(Intent.EXTRA_UID, INVALID_UID)
            if (intent.action == Intent.ACTION_PACKAGE_ADDED && !intent.getBooleanExtra(
                    Intent.EXTRA_REPLACING,
                    false
                ) && uid != INVALID_UID
            ) {
                if (PreferenceManager.getDefaultSharedPreferences(context!!)
                        ?.getBoolean("PREFERENCE_DEFAULT_INTERNET", true) == false
                ) {
                    context.getSystemService(NetworkPolicyManager::class.java)
                        .addUidPolicy(uid, NetworkPolicyManager.POLICY_REJECT_ALL)
                }
            }
        }
    }
}
