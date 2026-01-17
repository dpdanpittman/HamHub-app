package com.hamhub.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hamhub.app.ui.screens.adif.AdifScreen
import com.hamhub.app.ui.screens.awards.AwardsScreen
import com.hamhub.app.ui.screens.bandplan.BandDetailScreen
import com.hamhub.app.ui.screens.bandplan.BandPlanScreen
import com.hamhub.app.ui.screens.callsign.CallsignLookupScreen
import com.hamhub.app.ui.screens.dashboard.DashboardScreen
import com.hamhub.app.ui.screens.iss.IssTrackerScreen
import com.hamhub.app.ui.screens.logbook.AddEditQsoScreen
import com.hamhub.app.ui.screens.logbook.LogbookScreen
import com.hamhub.app.ui.screens.map.MapScreen
import com.hamhub.app.ui.screens.more.MoreScreen
import com.hamhub.app.ui.screens.propagation.PropagationScreen
import com.hamhub.app.ui.screens.repeaters.RepeatersScreen
import com.hamhub.app.ui.screens.settings.SettingsScreen

@Composable
fun HamHubNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Logbook.route,
        modifier = modifier
    ) {
        // Main screens
        composable(Screen.Logbook.route) {
            LogbookScreen(
                onAddQso = { navController.navigate(Screen.AddQso.route) },
                onEditQso = { qsoId -> navController.navigate(Screen.EditQso.createRoute(qsoId)) }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }

        composable(Screen.Awards.route) {
            AwardsScreen()
        }

        composable(Screen.Map.route) {
            MapScreen()
        }

        composable(Screen.More.route) {
            MoreScreen(
                onNavigate = { screen -> navController.navigate(screen.route) }
            )
        }

        // Secondary screens
        composable(Screen.Propagation.route) {
            PropagationScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.IssTracker.route) {
            IssTrackerScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Repeaters.route) {
            RepeatersScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.BandPlan.route) {
            BandPlanScreen(
                onBandClick = { band -> navController.navigate(Screen.BandDetail.createRoute(band)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.BandDetail.route,
            arguments = listOf(navArgument("band") { type = NavType.StringType })
        ) { backStackEntry ->
            val band = backStackEntry.arguments?.getString("band") ?: ""
            BandDetailScreen(
                band = band,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CallsignLookup.route) {
            CallsignLookupScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ImportExport.route) {
            AdifScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // QSO screens
        composable(Screen.AddQso.route) {
            AddEditQsoScreen(
                qsoId = null,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditQso.route,
            arguments = listOf(navArgument("qsoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val qsoId = backStackEntry.arguments?.getLong("qsoId")
            AddEditQsoScreen(
                qsoId = qsoId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}
