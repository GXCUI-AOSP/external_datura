/*
 * SPDX-FileCopyrightText: 2023 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package org.calyxos.datura

import android.app.Application
import android.content.Intent
import dagger.hilt.android.HiltAndroidApp
import org.calyxos.datura.service.ForegroundService

@HiltAndroidApp
class DaturaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startForegroundService(Intent(this, ForegroundService::class.java))
    }
}
