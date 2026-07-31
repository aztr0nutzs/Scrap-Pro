package com.example.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.core.ScreenHeaderBanner
import com.example.ui.navigation.Screen
import com.example.ui.theme.IndustrialOrange

private data class QuickAction(val label: String, val route: String, val icon: ImageVector)
private data class YardActivity(val yard: String, val summary: String, val amount: String)

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    val listState = rememberLazyListState()
    val actions = listOf(
        QuickAction("Profit", Screen.Profit.route, Icons.Filled.Calculate),
        QuickAction("Wire ROI", Screen.Wire.route, Icons.Filled.SettingsInputComponent),
        QuickAction("Payload", Screen.Payload.route, Icons.Filled.LocalShipping),
        QuickAction("Identify", Screen.Id.route, Icons.Filled.Search),
        QuickAction("Find Yards", Screen.Yards.route, Icons.Filled.Place)
    )
    val activity = listOf(
        YardActivity("Apex Metals", "42 lb bare bright copper", "$159.60"),
        YardActivity("Steel City Scrap", "680 lb prepared steel", "$54.40"),
        YardActivity("Quick Cash", "31 lb clean brass", "$65.10")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item { ScreenHeaderBanner(R.drawable.scrap_main, "ScrapPro dashboard") }
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("ACTIVE HAUL", style = MaterialTheme.typography.labelLarge, color = IndustrialOrange)
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Current load", fontWeight = FontWeight.Bold)
                            Text("753 lb", color = IndustrialOrange, fontWeight = FontWeight.Bold)
                        }
                        Text("Copper · Brass · Prepared steel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SnapshotCard("EST. GROSS", "$329.10", Modifier.weight(1f))
                    SnapshotCard("EST. NET", "$287.60", Modifier.weight(1f))
                }
                Spacer(Modifier.height(20.dp))
                Text("QUICK ACTIONS", style = MaterialTheme.typography.labelLarge, color = IndustrialOrange)
                Spacer(Modifier.height(8.dp))
                actions.chunked(2).forEach { rowActions ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowActions.forEach { action ->
                            Button(
                                onClick = { onNavigate(action.route) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(action.icon, contentDescription = null, tint = IndustrialOrange)
                                Text(action.label, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                        if (rowActions.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text("RECENT YARD ACTIVITY", style = MaterialTheme.typography.labelLarge, color = IndustrialOrange)
            }
        }
        items(activity) { item ->
            OutlinedCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 5.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(item.yard, fontWeight = FontWeight.Bold)
                        Text(item.summary, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(item.amount, color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SnapshotCard(label: String, value: String, modifier: Modifier = Modifier) {
    OutlinedCard(modifier = modifier) {
        Column(Modifier.padding(14.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}
