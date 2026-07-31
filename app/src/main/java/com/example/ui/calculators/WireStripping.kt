package com.example.ui.calculators
import com.example.R

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.domain.engine.WireStrippingRoiEngine
import com.example.domain.engine.WireStrippingResult
import com.example.domain.model.WireCategory
import com.example.ui.core.UiState
import com.example.ui.theme.Copper
import com.example.ui.theme.IndustrialOrange

data class WireStrippingUiState(
    val category: WireCategory = WireCategory.ROMEX,
    val weight: String = "50",
    val insulatedPrice: String = "1.20",
    val bareBrightPrice: String = "3.80",
    val speed: String = "20",
    val recoveryRate: String = "65",
    val machineCost: String = "0",
    val result: WireStrippingResult? = null
)

class WireStrippingViewModel : ViewModel() {
    private val engine = WireStrippingRoiEngine()

    private val _uiState = MutableStateFlow<UiState<WireStrippingUiState>>(UiState.Success(WireStrippingUiState()))
    val uiState: StateFlow<UiState<WireStrippingUiState>> = _uiState.asStateFlow()
    
    private var currentState = WireStrippingUiState()

    init {
        calculate()
    }

    fun updateInputs(
        category: WireCategory = currentState.category,
        weight: String = currentState.weight,
        insulatedPrice: String = currentState.insulatedPrice,
        bareBrightPrice: String = currentState.bareBrightPrice,
        speed: String = currentState.speed,
        recoveryRate: String = currentState.recoveryRate,
        machineCost: String = currentState.machineCost
    ) {
        currentState = currentState.copy(
            category = category,
            weight = weight,
            insulatedPrice = insulatedPrice,
            bareBrightPrice = bareBrightPrice,
            speed = speed,
            recoveryRate = recoveryRate,
            machineCost = machineCost
        )
        calculate()
    }

    private fun calculate() {
        try {
            val weightD = currentState.weight.toDoubleOrNull() ?: 0.0
            val insulatedPriceD = currentState.insulatedPrice.toDoubleOrNull() ?: 0.0
            val bareBrightPriceD = currentState.bareBrightPrice.toDoubleOrNull() ?: 0.0
            val speedD = currentState.speed.toDoubleOrNull() ?: 0.0
            
            val newResult = engine.calculateRoi(
                currentState.category, 
                weightD, 
                insulatedPriceD, 
                bareBrightPriceD, 
                speedD,
                currentState.recoveryRate.toDoubleOrNull() ?: 0.0,
                currentState.machineCost.toDoubleOrNull() ?: 0.0
            )
            currentState = currentState.copy(result = newResult)
            _uiState.value = UiState.Success(currentState)
        } catch (e: Exception) {
            _uiState.value = UiState.Error("Calculation failed")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WireStrippingScreen(viewModel: WireStrippingViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState())) {
            com.example.ui.core.ScreenHeaderBanner(R.drawable.scrap_calc, "Scrap Calculator")
            Column(modifier = Modifier.padding(16.dp)) {
                when (val state = uiState) {
                is UiState.Loading -> CircularProgressIndicator()
                is UiState.Error -> Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                is UiState.Success -> {
                    val data = state.data
                    
                    data.result?.let {
                        val isRecommended = it.recommendation == WireStrippingResult.Recommendation.RECOMMENDED_TO_STRIP
                        val recColor = if (isRecommended) Color(0xFF34D399) else IndustrialOrange
                        val recText = if (isRecommended) "STRIP IT" else "SELL INSULATED"
                        
                        OutlinedCard(
                            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, recColor.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    color = recColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, recColor.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = recText,
                                        color = recColor,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text("Effective Hourly Wage", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$${"%.2f".format(it.effectiveHourlyWage)}/hr", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text("Insulated Payout", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("$${"%.2f".format(it.insulatedPayout)}", style = MaterialTheme.typography.titleMedium)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Stripped Payout", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("$${"%.2f".format(it.strippedPayout)}", style = MaterialTheme.typography.titleMedium, color = Copper)
                                    }
                                }
                                Text(
                                    "Labor time: ${"%.2f".format(it.laborHours)} hr",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    it.machinePaybackLoads?.let { loads -> "Machine payback: ${"%.1f".format(loads)} similar loads" }
                                        ?: "Machine payback: enter machine cost",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline)
                                
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Net Value Gain", style = MaterialTheme.typography.bodyMedium)
                                    Text("+$${"%.2f".format(it.netValueGain)}", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Inputs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = data.category.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Wire Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            WireCategory.values().forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.displayName) },
                                    onClick = {
                                        viewModel.updateInputs(
                                            category = category,
                                            recoveryRate = (category.recoveryYieldPercentage * 100).toInt().toString()
                                        )
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = data.weight,
                            onValueChange = { viewModel.updateInputs(weight = it) },
                            label = { Text("Weight (lbs)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = data.speed,
                            onValueChange = { viewModel.updateInputs(speed = it) },
                            label = { Text("Strip Speed (lbs/hr)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = data.recoveryRate,
                            onValueChange = { viewModel.updateInputs(recoveryRate = it) },
                            label = { Text("Recovery rate (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = data.machineCost,
                            onValueChange = { viewModel.updateInputs(machineCost = it) },
                            label = { Text("Machine cost ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = data.insulatedPrice,
                            onValueChange = { viewModel.updateInputs(insulatedPrice = it) },
                            label = { Text("Insulated Price ($/lb)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = data.bareBrightPrice,
                            onValueChange = { viewModel.updateInputs(bareBrightPrice = it) },
                            label = { Text("Bare Bright ($/lb)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            } // Close inner padding column
        }
    }
}
