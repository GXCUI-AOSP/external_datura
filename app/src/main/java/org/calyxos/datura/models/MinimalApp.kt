/*
 * SPDX-FileCopyrightText: 2024 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package org.calyxos.datura.models

import android.graphics.Bitmap

data class MinimalApp(
    val name: String = String(),
    val packageName: String = String(),
    val icon: Bitmap = Bitmap.createBitmap(48, 48, Bitmap.Config.RGB_565),
    val uid: Int = -1,
    val requestsInternetPermission: Boolean = false,
    override val type: Type = Type.APP
) : DaturaItem
