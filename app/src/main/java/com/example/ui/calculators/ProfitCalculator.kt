package com.example.ui.calculators
import com.example.R

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.app.Application
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.domain.engine.ProfitCalculationEngine
import com.example.domain.engine.ProfitCalculationResult
import com.example.domain.model.LoadItem
import com.example.domain.model.MetalGrade
import com.example.domain.model.ProcessingExpense
import com.example.domain.model.ScrapItem
import com.example.ui.core.UiState
import com.example.ui.theme.Copper
import com.example.ui.theme.IndustrialOrange
import com.example.data.local.ScrapProDatabase
import com.example.data.repository.HomeRepository
import com.example.data.repository.RoomHomeRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val loadItems: List<ScrapItem> = emptyList(),
    val expense: ProcessingExpense = ProcessingExpense(),
    val result: ProfitCalculationResult? = null,
    val saveMessage: String? = null
)

class CalculatorViewModel private constructor(
    application: Application,
    private val homeRepository: HomeRepository
) : AndroidViewModel(application) {
    constructor(application: Application) : this(
        application,
        RoomHomeRepository(ScrapProDatabase.getDatabase(application).homeDao())
    )

    private val engine = ProfitCalculationEngine()

    private val _uiState = MutableStateFlow<UiState<CalculatorUiState>>(UiState.Success(CalculatorUiState()))
    val uiState: StateFlow<UiState<CalculatorUiState>> = _uiState.asStateFlow()

    private var currentState = CalculatorUiState()

    init {
        recalculate()
        viewModelScope.launch {
            runCatching { homeRepository.observeActiveHaul().first() }
                .onSuccess { haul ->
                    if (haul != null) {
                        currentState = currentState.copy(
                            loadItems = haul.items,
                            expense = haul.expense,
                            saveMessage = null
                        )
                        recalculate()
                    }
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unable to restore the active haul")
                }
        }
    }

    fun addItem(grade: MetalGrade) {
        val newItem = ScrapItem(grade, 0.0, grade.defaultPricePerLb, true)
        currentState = currentState.copy(loadItems = currentState.loadItems + newItem, saveMessage = null)
        recalculate()
    }
    
    fun removeItem(index: Int) {
        val updated = currentState.loadItems.toMutableList()
        if (index in updated.indices) {
            updated.removeAt(index)
            currentState = currentState.copy(loadItems = updated, saveMessage = null)
            recalculate()
        }
    }
    
    fun updateItem(index: Int, weight: Double? = null, isCleaned: Boolean? = null, price: Double? = null, yieldPercent: Double? = null) {
        val updated = currentState.loadItems.toMutableList()
        if (index in updated.indices) {
            val item = updated[index]
            updated[index] = item.copy(
                weightLbs = weight ?: item.weightLbs,
                isCleaned = isCleaned ?: item.isCleaned,
                pricePerLb = price ?: item.pricePerLb,
                recoverableYieldPercent = yieldPercent ?: item.recoverableYieldPercent
            )
            currentState = currentState.copy(loadItems = updated, saveMessage = null)
            recalculate()
        }
    }

    fun updateExpense(fuel: Double, tolls: Double, laborHours: Double, hourlyRate: Double) {
        currentState = currentState.copy(
            expense = ProcessingExpense(
                fuelCost = fuel.coerceAtLeast(0.0),
                tolls = tolls.coerceAtLeast(0.0),
                processingLaborHours = laborHours.coerceAtLeast(0.0),
                hourlyLaborRate = hourlyRate.coerceAtLeast(0.0)
            ),
            saveMessage = null
        )
        recalculate()
    }

    fun saveToActiveHaul() {
        viewModelScope.launch {
            runCatching { homeRepository.saveActiveHaul(currentState.loadItems, currentState.expense) }
                .onSuccess {
                    currentState = currentState.copy(saveMessage = "Saved to active haul")
                    recalculate()
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unable to save the active haul")
                }
        }
    }

    private fun recalculate() {
        try {
            val loadItem = LoadItem(currentState.loadItems, currentState.expense)
            val newResult = engine.calculateProfit(loadItem)
            currentState = currentState.copy(result = newResult)
            _uiState.value = UiState.Success(currentState)
        } catch (e: Exception) {
            _uiState.value = UiState.Error("Failed to calculate profit: ${e.message}")
        }
    }
}

@Composable
fun ProfitBreakdownBar(result: ProfitCalculationResult, modifier: Modifier = Modifier) {
    if (result.grossPayout <= 0) return
    
    val netRatio = (result.netProfit / result.grossPayout).toFloat().coerceIn(0f, 1f)
    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Net Profit: $${"%.2f".format(result.netProfit)}", color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
            Text("Expenses: $${"%.2f".format(result.totalExpenses)}", color = Color(0xFFEF4444))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Canvas(modifier = Modifier.fillMaxWidth().height(16.dp)) {
            // Expenses (Red)
            drawRoundRect(
                color = Color(0xFFEF4444),
                size = size,
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )
            // Net Profit (Green)
            drawRoundRect(
                color = Color(0xFF34D399),
                size = Size(size.width * netRatio, size.height),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfitCalculatorScreen(viewModel: CalculatorViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var fuel by rememberSaveable { mutableStateOf("0") }
    var tolls by rememberSaveable { mutableStateOf("0") }
    var laborHours by rememberSaveable { mutableStateOf("0") }
    var hourlyRate by rememberSaveable { mutableStateOf("25") }

    fun updateExpenses() {
        viewModel.updateExpense(
            fuel.toDoubleOrNull() ?: 0.0,
            tolls.toDoubleOrNull() ?: 0.0,
            laborHours.toDoubleOrNull() ?: 0.0,
            hourlyRate.toDoubleOrNull() ?: 0.0
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = IndustrialOrange
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Metal")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState())) {
            com.example.ui.core.ScreenHeaderBanner(R.drawable.scrap_calc, "Profit calculator")
            Column(modifier = Modifier.padding(16.dp)) {
                when (val state = uiState) {
                is UiState.Loading -> CircularProgressIndicator()
                is UiState.Error -> Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                is UiState.Success -> {
                    val data = state.data
                    val result = data.result
                    LaunchedEffect(data.expense) {
                        fuel = data.expense.fuelCost.toString()
                        tolls = data.expense.tolls.toString()
                        laborHours = data.expense.processingLaborHours.toString()
                        hourlyRate = data.expense.hourlyLaborRate.toString()
                    }

                    // Summary Card
                    OutlinedCard(
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Copper),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                            Text("Total Gross Payout", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$${"%.2f".format(result?.grossPayout ?: 0.0)}", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            result?.let { ProfitBreakdownBar(it) }
                            
                            if ((result?.valueLossWarning ?: 0.0) > 0) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Surface(
                                    color = Color(0x19EF4444),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33EF4444)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "Value Lost by Selling Dirty: $${"%.2f".format(result?.valueLossWarning)}", 
                                        color = Color(0xFFEF4444), 
                                        modifier = Modifier.padding(12.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Load Items", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    data.loadItems.forEachIndexed { index, item ->
                        OutlinedCard(
                            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val properties = com.example.ui.core.MetalAssetRegistry.getPropertiesForGrade(item.grade)
                                        Image(
                                            painter = painterResource(id = properties.drawableRes),
                                            contentDescription = item.grade.displayName,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .border(2.dp, properties.primaryColor, CircleShape),
                                            contentScale = ContentScale.Fit
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(item.grade.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = IndustrialOrange)
                                    }
                                    IconButton(onClick = { viewModel.removeItem(index) }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                OutlinedTextField(
                                    value = if (item.weightLbs == 0.0) "" else item.weightLbs.toString(),
                                    onValueChange = { viewModel.updateItem(index, weight = it.toDoubleOrNull() ?: 0.0) },
                                    label = { Text("Weight (lb)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Price: $${"%.2f".format(item.pricePerLb)}/lb", modifier = Modifier.weight(1f))
                                    Slider(
                                        value = item.pricePerLb.toFloat(),
                                        onValueChange = { viewModel.updateItem(index, price = it.toDouble()) },
                                        valueRange = 0f..5f,
                                        modifier = Modifier.weight(2f),
                                        colors = SliderDefaults.colors(thumbColor = Copper, activeTrackColor = Copper)
                                    )
                                }

                                Text("Recoverable yield: ${item.recoverableYieldPercent.toInt()}%")
                                Slider(
                                    value = item.recoverableYieldPercent.toFloat(),
                                    onValueChange = { viewModel.updateItem(index, yieldPercent = it.toDouble()) },
                                    valueRange = 0f..100f,
                                    colors = SliderDefaults.colors(thumbColor = IndustrialOrange, activeTrackColor = IndustrialOrange)
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Clean / Separated?", modifier = Modifier.weight(1f))
                                    Switch(
                                        checked = item.isCleaned,
                                        onCheckedChange = { viewModel.updateItem(index, isCleaned = it) },
                                        colors = SwitchDefaults.colors(checkedThumbColor = IndustrialOrange, checkedTrackColor = IndustrialOrange.copy(alpha = 0.5f))
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Expenses & Labor", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(fuel, { fuel = it; updateExpenses() }, label = { Text("Fuel ($)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                        OutlinedTextField(tolls, { tolls = it; updateExpenses() }, label = { Text("Other ($)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(laborHours, { laborHours = it; updateExpenses() }, label = { Text("Labor hours") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                        OutlinedTextField(hourlyRate, { hourlyRate = it; updateExpenses() }, label = { Text("$/hour") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                    }
                    Button(
                        onClick = viewModel::saveToActiveHaul,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(48.dp),
                        enabled = data.loadItems.isNotEmpty()
                    ) {
                        Text("Save to active haul")
                    }
                    data.saveMessage?.let { message ->
                        Text(
                            message,
                            color = Color(0xFF34D399),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
            } // Close inner padded column
        }
    }
    
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Metal to Load") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    MetalGrade.values().forEach { grade ->
                        com.example.ui.core.MetalSelectorCard(
                            grade = grade,
                            pricePerLb = grade.defaultPricePerLb,
                            onClick = {
                                viewModel.addItem(grade)
                                showAddDialog = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}
