package com.v1kth0rx.T0T1T0x.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.v1kth0rx.T0T1T0x.R

sealed class Screen(
    val route: String, 
    val titleResId: Int, 
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Profile : Screen("profile", R.string.tab_profile, Icons.Filled.Person, Icons.Outlined.Person)
    object Game : Screen("game", R.string.tab_game, Icons.Filled.PlayArrow, Icons.Outlined.PlayArrow)
    object Settings : Screen("settings", R.string.tab_settings, Icons.Filled.Settings, Icons.Outlined.Settings)
}

val items = listOf(
    Screen.Profile,
    Screen.Game,
    Screen.Settings
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TotitoBottomNav(navController: NavController, modifier: Modifier = Modifier) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ShortNavigationBar(modifier = modifier) {
        items.forEach { screen ->
            val isSelected = currentRoute == screen.route
            ShortNavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                        contentDescription = stringResource(id = screen.titleResId)
                    )
                },
                label = {
                    Text(text = stringResource(id = screen.titleResId))
                }
            )
        }
    }
}
