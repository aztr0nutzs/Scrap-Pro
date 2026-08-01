package com.example.ui.yardfinder

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.domain.ScrapYardLocation
import com.example.data.domain.YardFilters
import com.example.data.domain.YardPrice
import com.example.domain.model.MetalGrade
import java.util.concurrent.TimeUnit

fun buildDialIntent(phone: String): Intent? {
    val normalized = phone.filter { it.isDigit() || it == '+' }
    return normalized.takeIf { it.count(Char::isDigit) >= 7 }?.let { Intent(Intent.ACTION_DIAL, Uri.parse("tel:$it")) }
}

fun buildMapIntents(yard: ScrapYardLocation): List<Intent> {
    val label = Uri.encode(yard.name)
    val geo = Uri.parse("geo:0,0?q=${yard.latitude},${yard.longitude}($label)")
    val browser = Uri.parse("https://www.google.com/maps/search/?api=1&query=${yard.latitude},${yard.longitude}")
    return listOf(
        Intent(Intent.ACTION_VIEW, geo).setPackage("com.google.android.apps.maps"),
        Intent(Intent.ACTION_VIEW, browser)
    )
}

fun launchDialer(context: Context, phone: String): Boolean {
    val intent = buildDialIntent(phone) ?: return false
    if (intent.resolveActivity(context.packageManager) == null) return false
    context.startActivity(intent)
    return true
}

fun launchNativeGoogleMaps(context: Context, yard: ScrapYardLocation): Boolean {
    val intent = buildMapIntents(yard).firstOrNull { it.resolveActivity(context.packageManager) != null } ?: return false
    context.startActivity(intent)
    return true
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YardFinderScreen(viewModel: YardFinderViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var editing by remember { mutableStateOf<ScrapYardLocation?>(null) }
    var adding by remember { mutableStateOf(false) }
    var priceYard by remember { mutableStateOf<ScrapYardLocation?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val coarse = result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val fine = result[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val activity = context as? android.app.Activity
        val permanent = !coarse && activity != null &&
            !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
        viewModel.useLocation(coarse, fine, locationServicesEnabled(context), permanent)
    }
    LaunchedEffect(state.location) {
        if (state.location == LocationUiState.RequestPermission) {
            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { adding = true }) { Icon(Icons.Default.Add, "Add yard") }
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(bottom = 96.dp)) {
            item { com.example.ui.core.ScreenHeaderBanner(R.drawable.price_track, "Price Tracker") }
            item {
                Column(Modifier.padding(16.dp)) {
                    OutlinedTextField(state.search, viewModel::updateSearch, label = { Text("Search yards or addresses") }, modifier = Modifier.fillMaxWidth())
                    LocationPanel(state.location, onUse = {
                        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        if (coarse) viewModel.useLocation(true, fine, locationServicesEnabled(context), false)
                        else viewModel.requestLocationPermission()
                    })
                    FilterPanel(state.filters, viewModel::updateFilters)
                }
            }
            if (state.yards.isEmpty()) {
                item { EmptyDirectory(onAdd = { adding = true }, onImport = viewModel::importStarterDataset) }
            } else if (state.visibleYards.isEmpty()) {
                item { Text("No yards match the current search and filters.", Modifier.padding(24.dp)) }
            } else {
                items(state.visibleYards, key = { it.id }) { yard ->
                    YardCard(
                        yard = yard,
                        prices = state.prices[yard.id].orEmpty(),
                        onFavorite = { viewModel.toggleFavorite(yard) },
                        onEdit = { editing = yard },
                        onDelete = { viewModel.deleteYard(yard) },
                        onPrices = { priceYard = yard },
                        onCall = { if (!launchDialer(context, yard.phoneNumber)) viewModel.showMessage("No valid phone number or dialer is available") },
                        onNavigate = { if (!launchNativeGoogleMaps(context, yard)) viewModel.showMessage("No compatible maps or browser activity is available") }
                    )
                }
            }
        }
    }

    state.message?.let { message ->
        AlertDialog(onDismissRequest = viewModel::clearMessage, text = { Text(message) }, confirmButton = { TextButton(onClick = viewModel::clearMessage) { Text("OK") } })
    }
    if (adding || editing != null) {
        YardEditor(editing, onDismiss = { adding = false; editing = null }, onSave = { viewModel.saveYard(it); adding = false; editing = null })
    }
    priceYard?.let { yard ->
        PriceTrackerDialog(yard, state.prices[yard.id].orEmpty(), onDismiss = { priceYard = null }, onSave = viewModel::savePrice, onDelete = viewModel::deletePrice)
    }
}

@Composable
private fun LocationPanel(state: LocationUiState, onUse: () -> Unit) {
    val text = when (state) {
        LocationUiState.NotRequested -> "Location is optional. Saved yards are available without it."
        LocationUiState.RequestPermission -> "Choose approximate or precise location in the system prompt."
        LocationUiState.Loading -> "Finding your location…"
        is LocationUiState.Active -> if (state.approximate) "Distances use approximate location." else "Distances use precise location."
        LocationUiState.Denied -> "Location denied. The directory remains available."
        LocationUiState.PermanentlyDenied -> "Location is blocked. Enable it in app settings if desired."
        LocationUiState.ServicesDisabled -> "Location services are disabled."
        LocationUiState.Unavailable -> "Location is currently unavailable."
    }
    OutlinedCard(Modifier.fillMaxWidth().padding(top = 12.dp)) {
        Column(Modifier.padding(12.dp)) { Text(text); Button(onClick = onUse, Modifier.padding(top = 8.dp).height(48.dp)) { Text("Use My Location") } }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FilterPanel(filters: YardFilters, update: (YardFilters) -> Unit) {
    Column(Modifier.padding(top = 8.dp)) {
        Text("Filters", style = MaterialTheme.typography.titleMedium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FilterChip(filters.ferrous, { update(filters.copy(ferrous = !filters.ferrous)) }, { Text("Ferrous") })
            FilterChip(filters.nonFerrous, { update(filters.copy(nonFerrous = !filters.nonFerrous)) }, { Text("Non-ferrous") })
            FilterChip(filters.vehicles, { update(filters.copy(vehicles = !filters.vehicles)) }, { Text("Vehicles") })
            FilterChip(filters.eWaste, { update(filters.copy(eWaste = !filters.eWaste)) }, { Text("E-waste") })
            FilterChip(filters.openNow, { update(filters.copy(openNow = !filters.openNow)) }, { Text("Open now") })
            FilterChip(filters.truckScale, { update(filters.copy(truckScale = !filters.truckScale)) }, { Text("Truck scale") })
            FilterChip(filters.cash, { update(filters.copy(cash = !filters.cash)) }, { Text("Cash") })
            FilterChip(filters.check, { update(filters.copy(check = !filters.check)) }, { Text("Check") })
            FilterChip(filters.digital, { update(filters.copy(digital = !filters.digital)) }, { Text("Digital") })
            FilterChip(filters.favorites, { update(filters.copy(favorites = !filters.favorites)) }, { Text("Favorites") })
        }
        Text("Radius: ${filters.radiusMiles?.toInt()?.toString() ?: "Any"} miles")
        Slider((filters.radiusMiles ?: 100.0).toFloat(), { update(filters.copy(radiusMiles = if (it >= 100) null else it.toDouble())) }, valueRange = 5f..100f)
    }
}

@Composable
private fun EmptyDirectory(onAdd: () -> Unit, onImport: () -> Unit) {
    Column(Modifier.padding(24.dp)) {
        Text("No saved yards", style = MaterialTheme.typography.titleLarge)
        Text("Add a verified local yard, or optionally import clearly labeled starter templates to edit.")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 12.dp)) {
            Button(onClick = onAdd) { Text("Add yard") }
            OutlinedButton(onClick = onImport) { Text("Import starter templates") }
        }
    }
}

@Composable
private fun YardCard(yard: ScrapYardLocation, prices: List<YardPrice>, onFavorite: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit, onPrices: () -> Unit, onCall: () -> Unit, onNavigate: () -> Unit) {
    OutlinedCard(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) { Text(yard.name, style = MaterialTheme.typography.titleMedium); Text(yard.address) }
                IconButton(onClick = onFavorite) { Icon(if (yard.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, "Favorite") }
            }
            if (yard.bundledStarter) Text("BUNDLED STARTER — verify and edit before use", color = MaterialTheme.colorScheme.error)
            Text(yard.distanceMiles?.let { "%.1f miles${if (yard.distanceIsFallback) " (fallback origin)" else ""}".format(it) } ?: "Distance unavailable — use location")
            Text(if (yard.temporarilyClosed) "Temporarily/holiday closed" else yard.operatingHours.ifBlank { "Hours not recorded" })
            val latest = prices.firstOrNull()
            Text(latest?.let { "Latest user-reported price: $${"%.2f".format(it.price)}/${it.unit} · ${priceAge(it)}" } ?: "No user-reported prices")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(onClick = onNavigate, Modifier.weight(1f).height(48.dp)) { Text("NAVIGATE NOW") }
                OutlinedButton(onClick = onCall, Modifier.height(48.dp)) { Text("CALL") }
            }
            Row { TextButton(onClick = onPrices) { Text("Prices") }; IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Edit yard") }; IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete yard") } }
        }
    }
}

private fun priceAge(price: YardPrice, now: Long = System.currentTimeMillis()): String {
    val days = TimeUnit.MILLISECONDS.toDays((now - price.observedAt).coerceAtLeast(0))
    return "$days day${if (days == 1L) "" else "s"} old${if (price.isStale(now)) " · STALE" else ""}"
}

@Composable
private fun YardEditor(existing: ScrapYardLocation?, onDismiss: () -> Unit, onSave: (ScrapYardLocation) -> Unit) {
    var yard by remember { mutableStateOf(existing ?: ScrapYardLocation(name = "", address = "", latitude = 0.0, longitude = 0.0, phoneNumber = "", operatingHours = "", temporarilyClosed = false, acceptsFerrous = false, acceptsNonFerrous = false, acceptsVehicles = false, acceptsEWaste = false, cashPayout = false, checkPayout = false, digitalPayout = false, requiredId = "", hasTruckScale = false, notes = "", favorite = false, bundledStarter = false)) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text(if (existing == null) "Add yard" else "Edit yard") }, text = {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            EditorField("Yard name", yard.name) { yard = yard.copy(name = it) }; EditorField("Full address", yard.address) { yard = yard.copy(address = it) }
            EditorField("Latitude", yard.latitude.toString()) { yard = yard.copy(latitude = it.toDoubleOrNull() ?: 0.0) }; EditorField("Longitude", yard.longitude.toString()) { yard = yard.copy(longitude = it.toDoubleOrNull() ?: 0.0) }
            EditorField("Phone", yard.phoneNumber) { yard = yard.copy(phoneNumber = it) }; EditorField("Hours (MON=08:00-17:00;TUE=...)", yard.operatingHours) { yard = yard.copy(operatingHours = it) }
            EditorField("Required ID", yard.requiredId) { yard = yard.copy(requiredId = it) }; EditorField("Notes", yard.notes) { yard = yard.copy(notes = it) }
            CheckRow("Temporarily/holiday closed", yard.temporarilyClosed) { yard = yard.copy(temporarilyClosed = it) }; CheckRow("Ferrous", yard.acceptsFerrous) { yard = yard.copy(acceptsFerrous = it) }; CheckRow("Non-ferrous", yard.acceptsNonFerrous) { yard = yard.copy(acceptsNonFerrous = it) }; CheckRow("Vehicles", yard.acceptsVehicles) { yard = yard.copy(acceptsVehicles = it) }; CheckRow("E-waste", yard.acceptsEWaste) { yard = yard.copy(acceptsEWaste = it) }; CheckRow("Truck scale", yard.hasTruckScale) { yard = yard.copy(hasTruckScale = it) }; CheckRow("Cash", yard.cashPayout) { yard = yard.copy(cashPayout = it) }; CheckRow("Check", yard.checkPayout) { yard = yard.copy(checkPayout = it) }; CheckRow("Digital", yard.digitalPayout) { yard = yard.copy(digitalPayout = it) }
        }
    }, confirmButton = { TextButton(enabled = yard.name.isNotBlank() && yard.address.isNotBlank(), onClick = { onSave(yard.copy(bundledStarter = false)) }) { Text("Save") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}

@Composable private fun EditorField(label: String, value: String, set: (String) -> Unit) { OutlinedTextField(value, set, label = { Text(label) }, modifier = Modifier.fillMaxWidth()) }
@Composable private fun CheckRow(label: String, checked: Boolean, set: (Boolean) -> Unit) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label); Switch(checked, set) } }

@Composable
private fun PriceTrackerDialog(yard: ScrapYardLocation, prices: List<YardPrice>, onDismiss: () -> Unit, onSave: (YardPrice) -> Unit, onDelete: (YardPrice) -> Unit) {
    var grade by remember { mutableStateOf(MetalGrade.BARE_BRIGHT_COPPER.name) }; var value by remember { mutableStateOf("") }; var unit by remember { mutableStateOf("lb") }; var editId by remember { mutableStateOf(0L) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("${yard.name} prices") }, text = { Column(Modifier.verticalScroll(rememberScrollState())) {
        Text("User-reported records only — not live market data.")
        OutlinedTextField(grade, { grade = it }, label = { Text("Metal grade") }); OutlinedTextField(value, { value = it }, label = { Text("Price") })
        Row { FilterChip(unit == "lb", { unit = "lb" }, { Text("per lb") }); Spacer(Modifier.width(8.dp)); FilterChip(unit == "ton", { unit = "ton" }, { Text("per ton") }) }
        Button(enabled = value.toDoubleOrNull() != null, onClick = { onSave(YardPrice(id = editId, yardId = yard.id, metalGrade = grade, price = value.toDouble(), unit = unit, observedAt = System.currentTimeMillis())); value = ""; editId = 0 }) { Text(if (editId == 0L) "Add price" else "Save price edit") }
        prices.forEach { price -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("${price.metalGrade}: $${price.price}/${price.unit} · ${priceAge(price)}", Modifier.weight(1f)); IconButton(onClick = { editId = price.id; grade = price.metalGrade; value = price.price.toString(); unit = price.unit }) { Icon(Icons.Default.Edit, "Edit price") }; IconButton(onClick = { onDelete(price) }) { Icon(Icons.Default.Delete, "Delete price") } } }
    } }, confirmButton = { TextButton(onClick = onDismiss) { Text("Done") } })
}

private fun locationServicesEnabled(context: Context): Boolean {
    val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}
