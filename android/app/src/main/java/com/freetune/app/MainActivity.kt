package com.freetune.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.freetune.app.ui.ViewModelFactory
import com.freetune.app.ui.screens.auth.AuthScreen
import com.freetune.app.ui.screens.auth.AuthViewModel
import com.freetune.app.ui.screens.home.HomeScreen
import com.freetune.app.ui.screens.home.HomeViewModel
import com.freetune.app.ui.screens.library.LibraryScreen
import com.freetune.app.ui.screens.library.LibraryViewModel
import com.freetune.app.ui.screens.player.MiniPlayer
import com.freetune.app.ui.screens.player.PlayerScreen
import com.freetune.app.ui.screens.player.PlayerViewModel
import com.freetune.app.ui.screens.search.SearchScreen
import com.freetune.app.ui.screens.search.SearchViewModel
import com.freetune.app.ui.theme.FreeTuneTheme

private data class Tab(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val tabs = listOf(
    Tab("home", "Home", Icons.Filled.Home),
    Tab("search", "Search", Icons.Filled.Search),
    Tab("library", "Library", Icons.Filled.LibraryMusic),
)

class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        val app = application as FreeTuneApp
        app.playerController.connect()

        setContent {
            FreeTuneTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FreeTuneRoot(app)
                }
            }
        }
    }

    override fun onDestroy() {
        (application as FreeTuneApp).playerController.release()
        super.onDestroy()
    }
}

@Composable
private fun FreeTuneRoot(app: FreeTuneApp) {
    val factory = remember { ViewModelFactory(app) }
    val token by app.tokenStore.tokenFlow.collectAsState(initial = null)
    var isAuthChecked by remember { mutableStateOf(false) }

    LaunchedEffect(token) { isAuthChecked = true }

    if (!isAuthChecked) return

    if (token == null) {
        val authViewModel: AuthViewModel = viewModel(factory = factory)
        AuthScreen(authViewModel, onAuthenticated = { })
    } else {
        MainScreen(factory)
    }
}

@Composable
private fun MainScreen(factory: ViewModelFactory) {
    val navController = rememberNavController()
    val playerViewModel: PlayerViewModel = viewModel(factory = factory)
    var isPlayerExpanded by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                Column {
                    MiniPlayer(playerViewModel, onExpand = { isPlayerExpanded = true })
                    NavigationBar {
                        val backStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = backStackEntry?.destination
                        tabs.forEach { tab ->
                            NavigationBarItem(
                                selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                                onClick = {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(tab.icon, contentDescription = tab.label) },
                                label = { Text(tab.label) },
                            )
                        }
                    }
                }
            },
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(padding),
            ) {
                composable("home") {
                    val vm: HomeViewModel = viewModel(factory = factory)
                    HomeScreen(vm)
                }
                composable("search") {
                    val vm: SearchViewModel = viewModel(factory = factory)
                    SearchScreen(vm)
                }
                composable("library") {
                    val vm: LibraryViewModel = viewModel(factory = factory)
                    LibraryScreen(vm)
                }
            }
        }

        if (isPlayerExpanded) {
            PlayerScreen(playerViewModel, onCollapse = { isPlayerExpanded = false })
        }
    }
}
