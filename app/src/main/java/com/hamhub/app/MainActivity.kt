package com.hamhub.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hamhub.app.ui.navigation.HamHubNavHost
import com.hamhub.app.ui.navigation.Screen
import com.hamhub.app.ui.theme.HamHubTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val themeSetting by themeViewModel.theme.collectAsState()

            val darkTheme = when (themeSetting) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            HamHubTheme(darkTheme = darkTheme) {
                HamHubApp()
            }
        }
    }
}

data class BottomNavItem(
    val screen: Screen,
    val labelResId: Int,
    val icon: ImageVector
)

@Composable
fun HamHubApp() {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home, R.string.nav_home, Icons.Default.Home),
        BottomNavItem(Screen.Dashboard, R.string.nav_dashboard, Icons.Default.Dashboard),
        BottomNavItem(Screen.Logbook, R.string.nav_logbook, Icons.AutoMirrored.Filled.MenuBook),
        BottomNavItem(Screen.Map, R.string.nav_map, Icons.Default.Map),
        BottomNavItem(Screen.Awards, R.string.nav_awards, Icons.Default.EmojiEvents)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Determine if we should show the bottom bar
    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.screen.route
                        } == true

                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(stringResource(item.labelResId)) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        HamHubNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
