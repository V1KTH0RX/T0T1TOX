package com.v1kth0rx.T0T1T0x

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.v1kth0rx.T0T1T0x.ui.game.GameScreen
import com.v1kth0rx.T0T1T0x.ui.navigation.Screen
import com.v1kth0rx.T0T1T0x.ui.navigation.TotitoBottomNav
import com.v1kth0rx.T0T1T0x.ui.profile.ProfileScreen
import com.v1kth0rx.T0T1T0x.ui.settings.SettingsScreen
import com.v1kth0rx.T0T1T0x.ui.theme.AppPalette
import com.v1kth0rx.T0T1T0x.ui.theme.ThemeMode
import com.v1kth0rx.T0T1T0x.ui.theme.TotitoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentPalette by rememberSaveable { mutableStateOf(AppPalette.DINAMICO) }
            var currentThemeMode by rememberSaveable { mutableStateOf(ThemeMode.SISTEMA) }

            TotitoTheme(palette = currentPalette, themeMode = currentThemeMode) {
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
                            GameScreen()
                        }
                        composable(Screen.Settings.route) {
                            // Pasamos los estados para poder modificarlos desde ajustes temporalmente,
                            // o simplemente mostramos la pantalla
                            SettingsScreen()
                        }
                    }
                }
            }
        }
    }
}