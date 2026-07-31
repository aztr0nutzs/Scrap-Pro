package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Filled.Home)
    data object Profit : Screen("profit", "Profit", Icons.Filled.Calculate)
    data object Wire : Screen("wire", "Wire", Icons.Filled.SettingsInputComponent)
    data object Payload : Screen("payload", "Payload", Icons.Filled.LocalShipping)
    data object Id : Screen("id", "ID", Icons.Filled.Search)
    data object Yards : Screen("yards", "Yards", Icons.Filled.Place)

    companion object {
        val bottomTabs = listOf(Home, Profit, Wire, Payload, Id, Yards)
    }
}
