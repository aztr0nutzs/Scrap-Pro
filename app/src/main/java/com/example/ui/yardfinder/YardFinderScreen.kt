package com.example.ui.yardfinder
import com.example.R

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.domain.ScrapYardLocation
import com.example.ui.theme.Copper
import com.example.ui.theme.IndustrialOrange
import com.example.ui.theme.StainlessSilver
import androidx.compose.foundation.verticalScroll

fun launchNativeGoogleMaps(context: Context, latitude: Double, longitude: Double, label: String) {
    val uri = Uri.parse("geo:0,0?q=$latitude,$longitude(${Uri.encode(label)})")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        // Web fallback if app isn't installed
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"))
        if (webIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(webIntent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YardFinderScreen(
    viewModel: YardFinderViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            Column {
                com.example.ui.core.ScreenHeaderBanner(R.drawable.price_track, "Price Tracker")
                TopAppBar(
                    title = { 
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            placeholder = { Text("Search yards...", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            trailingIcon = {
                                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                    if (uiState.searchQuery.isNotEmpty()) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(uiState.filterOpenNow, viewModel::toggleFilterOpenNow, { Text("Open now") })
                    FilterChip(uiState.filterNonFerrous, viewModel::toggleFilterNonFerrous, { Text("Non-ferrous") })
                    FilterChip(uiState.filterTruckScale, viewModel::toggleFilterTruckScale, { Text("Truck scale") })
                    FilterChip(uiState.filterCashPayout, viewModel::toggleFilterCashPayout, { Text("Cash") })
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = IndustrialOrange)
            } else {
                YardListView(uiState) { viewModel.selectYard(it) }
            }
        }

        if (uiState.selectedYard != null) {
            YardDetailBottomSheet(yard = uiState.selectedYard!!, onDismiss = { viewModel.selectYard(null) })
        }
    }
}

@Composable
fun YardListView(uiState: YardFinderUiState, onYardClick: (ScrapYardLocation) -> Unit) {
    val context = LocalContext.current
    if (uiState.filteredYards.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No yards match your filters.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(uiState.filteredYards, key = { it.id }) { yard ->
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth().clickable { onYardClick(yard) },
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(yard.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(String.format("%.1f mi", yard.distanceMiles), style = MaterialTheme.typography.labelMedium, color = IndustrialOrange)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(yard.address, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        val isOpen = yard.isOpenNow()
                        Text(
                            if (isOpen) "OPEN NOW" else "CLOSED",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isOpen) Color(0xFF10B981) else Color(0xFFEF4444),
                            fontWeight = FontWeight.Bold
                        )
                        val todayHours = yard.openingHours[java.time.ZonedDateTime.now().dayOfWeek]
                        Text(
                            todayHours?.let { "Today: ${it.first}–${it.second}" } ?: "Closed today",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            buildList {
                                if (yard.acceptsFerrous) add("Ferrous")
                                if (yard.acceptsNonFerrous) add("Non-ferrous")
                                if (yard.acceptsVehicles) add("Vehicles")
                                if (yard.acceptsEWaste) add("E-waste")
                            }.joinToString(" · "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${yard.phoneNumber}")))
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null)
                                Text("CALL", modifier = Modifier.padding(start = 6.dp))
                            }
                            Button(
                                onClick = { launchNativeGoogleMaps(context, yard.latitude, yard.longitude, yard.name) },
                                modifier = Modifier.weight(1.6f),
                                colors = ButtonDefaults.buttonColors(containerColor = IndustrialOrange)
                            ) {
                                Icon(Icons.Default.Directions, contentDescription = null)
                                Text("NAVIGATE NOW", modifier = Modifier.padding(start = 6.dp), maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YardDetailBottomSheet(yard: ScrapYardLocation, onDismiss: () -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val context = LocalContext.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = modalBottomSheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(yard.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(yard.address, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                val isOpen = yard.isOpenNow()
                Text(
                    if (isOpen) "OPEN NOW" else "CLOSED",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isOpen) Color(0xFF10B981) else Color(0xFFEF4444),
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply { data = Uri.parse("tel:${yard.phoneNumber}") }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = IndustrialOrange)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Call")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Call")
                }
                
                Button(
                    onClick = {
                        launchNativeGoogleMaps(context, yard.latitude, yard.longitude, yard.name)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Copper)
                ) {
                    Icon(Icons.Default.Directions, contentDescription = "Navigate")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Directions")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))
            
            val today = java.time.ZonedDateTime.now(java.time.ZoneId.systemDefault()).dayOfWeek
            val hours = yard.openingHours[today]
            if (hours != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Today's Hours: ${hours.first} - ${hours.second}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Text("Facility Specs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            
            SpecRow("Payout Types", yard.payoutType)
            SpecRow("Required ID", yard.requiredID)
            SpecRow("Truck Scale", if (yard.hasTruckScale) "Yes (Drive-on)" else "No")
            SpecRow("Container Service", if (yard.containerRentalAvailable) "Available" else "Not Available")
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("Materials Accepted", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (yard.acceptsFerrous) com.example.ui.core.MetalCategoryChip(grade = com.example.domain.model.MetalGrade.HEAVY_MELTING_STEEL, selected = false, onClick = {})
                if (yard.acceptsNonFerrous) com.example.ui.core.MetalCategoryChip(grade = com.example.domain.model.MetalGrade.BARE_BRIGHT_COPPER, selected = false, onClick = {})
                if (yard.acceptsNonFerrous) com.example.ui.core.MetalCategoryChip(grade = com.example.domain.model.MetalGrade.CAST_ALUMINUM, selected = false, onClick = {})
                if (yard.acceptsVehicles) BadgeChip("Vehicles", Icons.Default.CheckCircle)
                if (yard.acceptsEWaste) BadgeChip("E-Waste", Icons.Default.CheckCircle)
            }
            
            if (yard.safetyGearRequired.isNotEmpty() || yard.prohibitedItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Rules & Safety", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                if (yard.safetyGearRequired.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Safety Gear Required:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text(yard.safetyGearRequired.joinToString(", "), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                if (yard.prohibitedItems.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Prohibited Items:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                            Text(yard.prohibitedItems.joinToString(", "), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpecRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BadgeChip(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
