/*
 * SPDX-FileCopyrightText: 2024 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package org.calyxos.datura.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import org.calyxos.datura.service.DaturaService

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    private val TAG = BootReceiver::class.java.simpleName

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startService(Intent(context, DaturaService::class.java))
    }
}