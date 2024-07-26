/*
 * SPDX-FileCopyrightText: 2023 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

package org.calyxos.datura.main

import android.os.Bundle
import android.os.UserManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.AndroidEntryPoint
import org.calyxos.datura.R

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DynamicColors.applyToActivityIfAvailable(this)
        setContentView(R.layout.activity_main)

        if (getSystemService(UserManager::class.java)?.isManagedProfile == true) {
            navigateToWorkFragment()
        }
    }

    private fun navigateToWorkFragment() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.navigation_resource, true)
            .build()
        navOptions.shouldLaunchSingleTop()

        navController.navigate(R.id.workFragment, null, navOptions)
    }
}
