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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.domain.model.ActiveHaul
import com.example.domain.model.RecentHaulActivity
import com.example.ui.core.ScreenHeaderBanner
import com.example.ui.navigation.Screen
import com.example.ui.theme.IndustrialOrange
import java.text.DateFormat
import java.util.Date

private data class QuickAction(val label: String, val route: String, val icon: ImageVector)

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var confirmClear by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val actions = remember {
        listOf(
            QuickAction("Profit", Screen.Profit.route, Icons.Filled.Calculate),
            QuickAction("Wire ROI", Screen.Wire.route, Icons.Filled.SettingsInputComponent),
            QuickAction("Payload", Screen.Payload.route, Icons.Filled.LocalShipping),
            QuickAction("Identify", Screen.Id.route, Icons.Filled.Search),
            QuickAction("Find Yards", Screen.Yards.route, Icons.Filled.Place)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item { ScreenHeaderBanner(R.drawable.scrap_main, "ScrapPro dashboard") }
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                when (val current = state) {
                    HomeUiState.Loading -> LoadingDashboard()
                    is HomeUiState.Error -> ErrorDashboard(current.message, viewModel::retry)
                    is HomeUiState.Empty -> EmptyDashboard(viewModel::startHaul)
                    is HomeUiState.Populated -> ActiveHaulCard(
                        haul = current.activeHaul,
                        onEdit = { onNavigate(Screen.Profit.route) },
                        onComplete = viewModel::completeHaul,
                        onClear = { confirmClear = true }
                    )
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
                                modifier = Modifier.weight(1f).height(48.dp),
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
                Text("RECENT HAUL ACTIVITY", style = MaterialTheme.typography.labelLarge, color = IndustrialOrange)
            }
        }

        val recent = when (val current = state) {
            is HomeUiState.Empty -> current.recentActivity
            is HomeUiState.Populated -> current.recentActivity
            else -> emptyList()
        }
        if (recent.isEmpty()) {
            item {
                Text(
                    "Completed hauls will appear here after you log your first haul.",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(recent, key = { it.id }) { activity -> RecentActivityCard(activity) }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text("Clear active haul?") },
            text = { Text("This removes the active haul and its metal items. Completed haul history is not affected.") },
            confirmButton = {
                TextButton(onClick = { confirmClear = false; viewModel.clearHaul() }) {
                    Text("Clear haul", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun LoadingDashboard() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator()
        Text("Loading haul data…", modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun ErrorDashboard(message: String, onRetry: () -> Unit) {
    OutlinedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Dashboard unavailable", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            Text(message, modifier = Modifier.padding(vertical = 8.dp))
            Button(onClick = onRetry) { Text("Retry") }
        }
    }
}

@Composable
private fun EmptyDashboard(onStart: () -> Unit) {
    OutlinedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("No active haul", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "Start a haul, then open Profit to add metal items, expenses, and labor. Your progress is saved on this device.",
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onStart, modifier = Modifier.height(48.dp)) { Text("Start a haul") }
        }
    }
}

@Composable
private fun ActiveHaulCard(
    haul: ActiveHaul,
    onEdit: () -> Unit,
    onComplete: () -> Unit,
    onClear: () -> Unit
) {
    Text("ACTIVE HAUL", style = MaterialTheme.typography.labelLarge, color = IndustrialOrange)
    Spacer(Modifier.height(8.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${haul.items.size} metal item${if (haul.items.size == 1) "" else "s"}", fontWeight = FontWeight.Bold)
                Text("${"%.1f".format(haul.totalWeightLbs)} lb", color = IndustrialOrange, fontWeight = FontWeight.Bold)
            }
            if (haul.items.isEmpty()) {
                Text("Open Profit to add the first metal item.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Text(haul.items.joinToString(" · ") { it.grade.displayName }, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SnapshotCard("EST. GROSS", money(haul.estimatedGrossPayout), Modifier.weight(1f))
                SnapshotCard("EXPENSES", money(haul.expenses), Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SnapshotCard("LABOR", money(haul.laborDeduction), Modifier.weight(1f))
                SnapshotCard("EST. NET", money(haul.estimatedNetProfit), Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onEdit, modifier = Modifier.weight(1f).height(48.dp)) { Text("Edit haul") }
                Button(
                    onClick = onComplete,
                    enabled = haul.items.isNotEmpty(),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) { Text("Complete") }
            }
            OutlinedButton(onClick = onClear, modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(48.dp)) {
                Text("Clear haul")
            }
        }
    }
}

@Composable
private fun RecentActivityCard(activity: RecentHaulActivity) {
    OutlinedCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 5.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(activity.yardName ?: "Completed haul", fontWeight = FontWeight.Bold)
                Text(
                    "${"%.1f".format(activity.totalWeightLbs)} lb · ${DateFormat.getDateInstance().format(Date(activity.completedAt))}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(money(activity.netProfit), color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SnapshotCard(label: String, value: String, modifier: Modifier = Modifier) {
    OutlinedCard(modifier = modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

private fun money(value: Double): String = "$${"%.2f".format(value)}"
