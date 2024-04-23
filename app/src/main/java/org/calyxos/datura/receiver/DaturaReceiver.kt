/*
 * SPDX-FileCopyrightText: 2024 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package org.calyxos.datura.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkPolicyManager
import android.os.Process
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import org.calyxos.datura.service.DaturaService
import javax.inject.Inject

@AndroidEntryPoint
class DaturaReceiver : BroadcastReceiver() {

    @Inject
    lateinit var netPolicyManager: NetworkPolicyManager

    private val TAG = DaturaReceiver::class.java.simpleName

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent?.action == DaturaService.ACTION_ALLOW_INTERNET_ACCESS) {
            val packageName = intent.getStringExtra(Intent.EXTRA_PACKAGE_NAME)
            val uid = intent.getIntExtra(Intent.EXTRA_UID, Process.INVALID_UID)

            if (uid != Process.INVALID_UID) {
                Log.i(TAG, "Allowing internet access for $uid")
                netPolicyManager.removeUidPolicy(uid, NetworkPolicyManager.POLICY_REJECT_ALL)
                context.getSystemService(NotificationManager::class.java)
                    ?.cancel(packageName!!.hashCode() + uid)
            }
        }
    }
}
