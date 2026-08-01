package com.example.ui.calculators
import com.example.R

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CalculatorsTabScreen() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Load Profit", "Wire Stripping")

    Column(modifier = Modifier.fillMaxSize()) {
        com.example.ui.core.ScreenHeaderBanner(R.drawable.scrap_main, "Main Dashboard")
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTabIndex) {
                0 -> ProfitCalculatorScreen()
                1 -> WireStrippingScreen()
            }
        }
    }
}
