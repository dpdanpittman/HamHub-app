package com.hamhub.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    // Main navigation items (bottom bar)
    data object Logbook : Screen("logbook", "Logbook", Icons.AutoMirrored.Filled.List)
    data object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    data object Awards : Screen("awards", "Awards", Icons.Default.EmojiEvents)
    data object Map : Screen("map", "Map", Icons.Default.Map)
    data object More : Screen("more", "More", Icons.Default.MoreHoriz)

    // Secondary screens (from More menu)
    data object Propagation : Screen("propagation", "Propagation", Icons.Default.WbSunny)
    data object IssTracker : Screen("iss", "ISS Tracker", Icons.Default.Satellite)
    data object Repeaters : Screen("repeaters", "Repeaters", Icons.Default.CellTower)
    data object BandPlan : Screen("bandplan", "Band Plan", Icons.Default.TableChart)
    data object CallsignLookup : Screen("callsign", "Callsign Lookup", Icons.Default.Search)
    data object Resources : Screen("resources", "Resources", Icons.Default.MenuBook)
    data object Calculators : Screen("calculators", "Calculators", Icons.Default.Calculate)
    data object OtherServices : Screen("other_services", "Other Services", Icons.Default.Radio)
    data object Contests : Screen("contests", "Contests", Icons.Default.EmojiEvents)
    data object News : Screen("news", "News", Icons.Default.Newspaper)
    data object ImportExport : Screen("import_export", "Import / Export", Icons.Default.SwapVert)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)

    // Detail screens
    data object QsoDetail : Screen("qso/{qsoId}", "QSO Detail", Icons.Default.Info) {
        fun createRoute(qsoId: Long) = "qso/$qsoId"
    }
    data object AddQso : Screen("qso/add", "Add QSO", Icons.Default.Add)
    data object EditQso : Screen("qso/edit/{qsoId}", "Edit QSO", Icons.Default.Edit) {
        fun createRoute(qsoId: Long) = "qso/edit/$qsoId"
    }
    data object BandDetail : Screen("bandplan/{band}", "Band Detail", Icons.Default.TableChart) {
        fun createRoute(band: String) = "bandplan/$band"
    }

    companion object {
        val bottomNavItems = listOf(Logbook, Dashboard, Awards, Map, More)

        val moreMenuItems = listOf(
            Propagation,
            IssTracker,
            Repeaters,
            BandPlan,
            CallsignLookup,
            Resources,
            Calculators,
            OtherServices,
            Contests,
            News,
            ImportExport,
            Settings
        )
    }
}
