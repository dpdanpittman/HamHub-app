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
import com.hamhub.app.ui.screens.resources.ResourcesScreen
import com.hamhub.app.ui.screens.otherservices.OtherServicesScreen
import com.hamhub.app.ui.screens.calculators.CalculatorsScreen
import com.hamhub.app.ui.screens.contests.ContestsScreen
import com.hamhub.app.ui.screens.news.NewsScreen
import com.hamhub.app.ui.screens.guide.GuideScreen
import com.hamhub.app.ui.screens.settings.SettingsScreen
import com.hamhub.app.ui.screens.spotter.SpotterScreen
import com.hamhub.app.ui.screens.spotter.SpotterListDetailScreen

@Composable
fun HamHubNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
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

        composable(Screen.Home.route) {
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

        composable(Screen.Spotter.route) {
            SpotterScreen(
                onBack = { navController.popBackStack() },
                onListClick = { listId ->
                    navController.navigate(Screen.SpotterListDetail.createRoute(listId))
                }
            )
        }

        composable(
            route = Screen.SpotterListDetail.route,
            arguments = listOf(navArgument("listId") { type = NavType.LongType })
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getLong("listId") ?: 0L
            SpotterListDetailScreen(
                listId = listId,
                onBack = { navController.popBackStack() },
                onCallsignClick = { callsign ->
                    // Navigate to callsign lookup with the callsign pre-filled
                    navController.navigate(Screen.CallsignLookup.route)
                }
            )
        }

        composable(Screen.Resources.route) {
            ResourcesScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.OtherServices.route) {
            OtherServicesScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Calculators.route) {
            CalculatorsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Contests.route) {
            ContestsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.News.route) {
            NewsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Guide.route) {
            GuideScreen(
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
