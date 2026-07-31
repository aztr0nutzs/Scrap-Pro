package com.example.ui.calculators
import com.example.R

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.domain.engine.PayloadSafetyEngine
import com.example.domain.engine.PayloadSafetyResult
import com.example.domain.model.CargoLoad
import com.example.domain.model.VehicleRating
import com.example.ui.core.UiState

data class PayloadSafetyUiState(
    val truckMaxPayload: String = "1500",
    val trailerGvwr: String = "7000",
    val trailerEmptyWeight: String = "2000",
    val truckCargo: String = "1000",
    val trailerCargo: String = "3000",
    val tongueWeight: String = "600",
    val result: PayloadSafetyResult? = null,
    val warningAlert: String? = null
)

class PayloadSafetyViewModel : ViewModel() {
    private val engine = PayloadSafetyEngine()

    private val _uiState = MutableStateFlow<UiState<PayloadSafetyUiState>>(UiState.Success(PayloadSafetyUiState()))
    val uiState: StateFlow<UiState<PayloadSafetyUiState>> = _uiState.asStateFlow()
    
    private var currentState = PayloadSafetyUiState()

    init {
        calculate()
    }

    fun updateInputs(
        truckMaxPayload: String = currentState.truckMaxPayload,
        trailerGvwr: String = currentState.trailerGvwr,
        trailerEmptyWeight: String = currentState.trailerEmptyWeight,
        truckCargo: String = currentState.truckCargo,
        trailerCargo: String = currentState.trailerCargo,
        tongueWeight: String = currentState.tongueWeight
    ) {
        currentState = currentState.copy(
            truckMaxPayload = truckMaxPayload,
            trailerGvwr = trailerGvwr,
            trailerEmptyWeight = trailerEmptyWeight,
            truckCargo = truckCargo,
            trailerCargo = trailerCargo,
            tongueWeight = tongueWeight
        )
        calculate()
    }

    private fun calculate() {
        try {
            val rating = VehicleRating(
                currentState.truckMaxPayload.toDoubleOrNull() ?: 0.0, 
                currentState.trailerGvwr.toDoubleOrNull() ?: 0.0, 
                currentState.trailerEmptyWeight.toDoubleOrNull() ?: 0.0
            )
            val load = CargoLoad(
                currentState.truckCargo.toDoubleOrNull() ?: 0.0, 
                currentState.trailerCargo.toDoubleOrNull() ?: 0.0,
                currentState.tongueWeight.toDoubleOrNull() ?: 0.0
            )
            val newResult = engine.evaluatePayloadSafety(rating, load)
            
            // Emits warning alerts when approaching 90%+ capacity
            val warning = if (
                newResult.truckPayloadPercentage >= 90.0 ||
                newResult.trailerPayloadPercentage >= 90.0 ||
                (newResult.loadedTrailerWeightLbs > 0.0 && newResult.tongueWeightPercentage !in 10.0..15.0)
            ) {
                "WARNING: Approaching or exceeding safe payload limits (90%+ capacity)!"
            } else null
            
            currentState = currentState.copy(result = newResult, warningAlert = warning)
            _uiState.value = UiState.Success(currentState)
        } catch (e: Exception) {
            _uiState.value = UiState.Error("Failed to calculate payload safety")
        }
    }
}

@Composable
fun CapacityMeter(title: String, percentage: Double, currentLoad: Double, maxLoad: Double) {
    val meterColor = when {
        percentage < 80.0 -> Color(0xFF34D399) // Green
        percentage < 100.0 -> Color(0xFFFBBF24) // Yellow
        else -> Color(0xFFEF4444) // Red
    }
    
    val safePercentage = (percentage.toFloat() / 100f).coerceIn(0f, 1f)
    
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${currentLoad.toInt()} / ${maxLoad.toInt()} lbs (${"%.1f".format(percentage)}%)", 
                style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = meterColor)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Canvas(modifier = Modifier.fillMaxWidth().height(12.dp)) {
            // Background
            drawRoundRect(
                color = Color(0xFF27272A),
                size = size,
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
            // Foreground
            drawRoundRect(
                color = meterColor,
                size = Size(size.width * safePercentage, size.height),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayloadSafetyScreen(viewModel: PayloadSafetyViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState())) {
            com.example.ui.core.ScreenHeaderBanner(R.drawable.load_bal, "Load Balancer")
            Column(modifier = Modifier.padding(16.dp)) {
                when (val state = uiState) {
                is UiState.Loading -> CircularProgressIndicator()
                is UiState.Error -> Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                is UiState.Success -> {
                    val data = state.data
                    val result = data.result

                    OutlinedCard(
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Real-Time Load Balancer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            result?.let {
                                val truckMax = data.truckMaxPayload.toDoubleOrNull() ?: 0.0
                                val truckCargo = data.truckCargo.toDoubleOrNull() ?: 0.0
                                CapacityMeter("Truck Bed Payload", it.truckPayloadPercentage, truckCargo, truckMax)
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                val trailerGvwr = data.trailerGvwr.toDoubleOrNull() ?: 0.0
                                val trailerEmpty = data.trailerEmptyWeight.toDoubleOrNull() ?: 0.0
                                val trailerMax = trailerGvwr - trailerEmpty
                                val trailerCargo = data.trailerCargo.toDoubleOrNull() ?: 0.0
                                CapacityMeter("Trailer Load", it.trailerPayloadPercentage, trailerCargo, trailerMax)
                                Spacer(modifier = Modifier.height(12.dp))
                                val tongueColor = when {
                                    it.tongueWeightPercentage in 10.0..15.0 -> Color(0xFF34D399)
                                    it.tongueWeightPercentage in 8.0..17.0 -> Color(0xFFFBBF24)
                                    else -> Color(0xFFEF4444)
                                }
                                Text(
                                    "Tongue weight: ${"%.1f".format(it.tongueWeightPercentage)}%",
                                    color = tongueColor,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Safe zone: ${it.minimumSafeTongueWeightLbs.toInt()}–${it.maximumSafeTongueWeightLbs.toInt()} lb (10%–15%)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                LinearProgressIndicator(
                                    progress = { (it.tongueWeightPercentage / 20.0).toFloat().coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    color = tongueColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    data.warningAlert?.let {
                        Surface(
                            color = Color(0x19EF4444),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33EF4444)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = it, 
                                color = Color(0xFFEF4444), 
                                style = MaterialTheme.typography.bodyMedium, 
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    result?.let {
                        if (it.recommendations.isNotEmpty()) {
                            OutlinedCard(
                                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Placement Recommendations", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    it.recommendations.forEach { rec ->
                                        val isCritical = rec.startsWith("CRITICAL")
                                        Text("• $rec", 
                                            color = if (isCritical) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    Text("Vehicle Configurations (lbs)", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = data.truckMaxPayload,
                            onValueChange = { viewModel.updateInputs(truckMaxPayload = it) },
                            label = { Text("Truck Max Payload") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = data.truckCargo,
                            onValueChange = { viewModel.updateInputs(truckCargo = it) },
                            label = { Text("Truck Cargo") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = data.trailerGvwr,
                            onValueChange = { viewModel.updateInputs(trailerGvwr = it) },
                            label = { Text("Trailer GVWR") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = data.trailerEmptyWeight,
                            onValueChange = { viewModel.updateInputs(trailerEmptyWeight = it) },
                            label = { Text("Trailer Empty Wt") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = data.trailerCargo,
                        onValueChange = { viewModel.updateInputs(trailerCargo = it) },
                        label = { Text("Trailer Cargo") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = data.tongueWeight,
                        onValueChange = { viewModel.updateInputs(tongueWeight = it) },
                        label = { Text("Measured Tongue Weight (lb)") },
                        supportingText = { Text("Use a tongue scale; target 10%–15% of loaded trailer weight.") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            } // Close inner padding column
        }
    }
}
