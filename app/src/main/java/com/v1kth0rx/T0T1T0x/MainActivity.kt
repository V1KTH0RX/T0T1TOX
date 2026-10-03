package com.v1kth0rx.T0T1T0x

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.v1kth0rx.T0T1T0x.data.AppSettings
import com.v1kth0rx.T0T1T0x.data.DataStoreSettingsRepository
import com.v1kth0rx.T0T1T0x.data.dataStore
import com.v1kth0rx.T0T1T0x.ui.game.GameScreen
import com.v1kth0rx.T0T1T0x.ui.game.GameViewModel
import com.v1kth0rx.T0T1T0x.ui.navigation.Screen
import com.v1kth0rx.T0T1T0x.ui.navigation.TotitoBottomNav
import com.v1kth0rx.T0T1T0x.ui.profile.ProfileScreen
import com.v1kth0rx.T0T1T0x.ui.settings.SettingsScreen
import com.v1kth0rx.T0T1T0x.ui.settings.SettingsViewModel
import com.v1kth0rx.T0T1T0x.ui.theme.TotitoTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val settingsRepository = DataStoreSettingsRepository(this.dataStore)
        
        var isLoading = true
        val startTime = System.currentTimeMillis()
        
        lifecycleScope.launch {
            withTimeoutOrNull(1500) {
                settingsRepository.appSettings.first()
            }
            isLoading = false
        }
        
        splashScreen.setKeepOnScreenCondition {
            isLoading && (System.currentTimeMillis() - startTime) < 1500
        }
        
        setContent {
            val appSettings by settingsRepository.appSettings.collectAsState(initial = null)

            val safeSettings = appSettings ?: AppSettings()

            TotitoTheme(palette = safeSettings.palette, themeMode = safeSettings.themeMode) {
                val navController = rememberNavController()
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        TotitoBottomNav(navController = navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Game.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Profile.route) {
                            ProfileScreen()
                        }
                        composable(Screen.Game.route) {
                            GameScreen(
                                iconStyle = safeSettings.iconStyle,
                                viewModel = viewModel(factory = GameViewModel.provideFactory(settingsRepository))
                            )
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                viewModel = viewModel(factory = SettingsViewModel.provideFactory(settingsRepository))
                            )
                        }
                    }
                }
            }
        }
    }
}