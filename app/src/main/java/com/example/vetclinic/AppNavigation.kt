package com.example.vetclinic

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.vetclinic.ui.theme.BeigeText
import kotlin.collections.contains

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val items = listOf(
        BottomNavigationItem("profile", "Профиль", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavigationItem("appointments", "Записи", Icons.Filled.Create, Icons.Outlined.Create),
        BottomNavigationItem("sos", "SOS", Icons.Filled.Warning, Icons.Outlined.Warning)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedItemIndex by remember(navBackStackEntry) {
        derivedStateOf {
            items.indexOfFirst { it.route == currentRoute }
        }
    }

    val showBottomBar = currentRoute in items.map { it.route }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color(0xFF1E1E1E),
                    contentColor = BeigeText,
                    tonalElevation = 0.dp
                ) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = index == selectedItemIndex,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo("open_screen") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (index == selectedItemIndex) {
                                        item.selectedIcon
                                    } else {
                                        item.unselectedIcon
                                    },
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "open_screen",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("open_screen") { OpenScreen(navController) }
            composable("add_pet") { AddPet(navController) }
            composable("profile") { Profile(navController) }
            composable("appointments") { Appointments(navController) }
            composable("sos") { SOS(navController) }
        }
    }
}
