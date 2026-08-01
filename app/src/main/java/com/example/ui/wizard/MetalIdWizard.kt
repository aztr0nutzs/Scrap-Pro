package com.example.ui.wizard
import com.example.R

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.ui.theme.Copper
import com.example.ui.theme.IndustrialOrange

enum class WizardStep {
    MAGNET_TEST,
    COLOR_TEST,
    ACID_TEST,
    DENSITY_TEST,
    SPARK_TEST,
    RESULT
}

class MetalIdWizardViewModel : ViewModel() {
    private val _currentStep = MutableStateFlow(WizardStep.MAGNET_TEST)
    val currentStep: StateFlow<WizardStep> = _currentStep

    private val _identifiedMetal = MutableStateFlow("")
    val identifiedMetal: StateFlow<String> = _identifiedMetal
    
    private val _metalClassification = MutableStateFlow("")
    val metalClassification: StateFlow<String> = _metalClassification

    fun answerMagnet(sticks: Boolean) {
        if (sticks) {
            _currentStep.value = WizardStep.SPARK_TEST
        } else {
            _currentStep.value = WizardStep.COLOR_TEST
        }
    }

    private val _identifiedMetalGrade = MutableStateFlow<com.example.domain.model.MetalGrade?>(null)
    val identifiedMetalGrade: StateFlow<com.example.domain.model.MetalGrade?> = _identifiedMetalGrade

    fun answerColor(color: String) {
        when (color) {
            "Red/Orange" -> {
                _identifiedMetal.value = "Copper (Bare Bright, #1 or #2)"
                _metalClassification.value = "High-Value Non-Ferrous"
                _identifiedMetalGrade.value = com.example.domain.model.MetalGrade.BARE_BRIGHT_COPPER
                _currentStep.value = WizardStep.RESULT
            }
            "Yellow" -> {
                _identifiedMetal.value = "Brass"
                _metalClassification.value = "High-Value Non-Ferrous"
                _identifiedMetalGrade.value = com.example.domain.model.MetalGrade.BRASS
                _currentStep.value = WizardStep.RESULT
            }
            "Silver/Grey" -> {
                _currentStep.value = WizardStep.ACID_TEST
            }
        }
    }

    fun answerAcid(reacts: Boolean) {
        if (reacts) {
            _identifiedMetal.value = "Zinc / Galvanized Alloy"
            _metalClassification.value = "Lower-Value Non-Ferrous — verify with yard"
            _identifiedMetalGrade.value = com.example.domain.model.MetalGrade.CAST_ALUMINUM
            _currentStep.value = WizardStep.RESULT
        } else {
            _currentStep.value = WizardStep.DENSITY_TEST
        }
    }

    fun answerDensity(isHeavy: Boolean) {
        if (isHeavy) {
            _identifiedMetal.value = "Lead or Non-Magnetic Stainless Steel"
            _metalClassification.value = "Mid-Value Non-Ferrous"
            _identifiedMetalGrade.value = com.example.domain.model.MetalGrade.STAINLESS_STEEL
        } else {
            _identifiedMetal.value = "Aluminum (Cast or Sheet)"
            _metalClassification.value = "Mid-Value Non-Ferrous"
            _identifiedMetalGrade.value = com.example.domain.model.MetalGrade.CAST_ALUMINUM
        }
        _currentStep.value = WizardStep.RESULT
    }
    
    fun answerSpark(isLongAndBright: Boolean) {
        if (isLongAndBright) {
            _identifiedMetal.value = "Carbon Steel / High-Carbon Iron"
            _metalClassification.value = "Low-Value Ferrous"
            _identifiedMetalGrade.value = com.example.domain.model.MetalGrade.HEAVY_MELTING_STEEL
        } else {
            _identifiedMetal.value = "Cast Iron or Wrought Iron"
            _metalClassification.value = "Low-Value Ferrous"
            _identifiedMetalGrade.value = com.example.domain.model.MetalGrade.HEAVY_MELTING_STEEL
        }
        _currentStep.value = WizardStep.RESULT
    }

    fun reset() {
        _currentStep.value = WizardStep.MAGNET_TEST
        _identifiedMetal.value = ""
        _metalClassification.value = ""
        _identifiedMetalGrade.value = null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetalIdWizardScreen(viewModel: MetalIdWizardViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val step by viewModel.currentStep.collectAsState()
    val result by viewModel.identifiedMetal.collectAsState()
    val classification by viewModel.metalClassification.collectAsState()
    val grade by viewModel.identifiedMetalGrade.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState())) {
            com.example.ui.core.ScreenHeaderBanner(R.drawable.id_wizard, "ID Wizard")
            Column(modifier = Modifier.padding(16.dp)) {
                // Progress Indicator (Basic)
            val progress = when(step) {
                WizardStep.MAGNET_TEST -> 0.25f
                WizardStep.COLOR_TEST, WizardStep.SPARK_TEST -> 0.4f
                WizardStep.ACID_TEST -> 0.6f
                WizardStep.DENSITY_TEST -> 0.75f
                WizardStep.RESULT -> 1.0f
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = IndustrialOrange,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Spacer(modifier = Modifier.height(24.dp))

            when (step) {
                WizardStep.MAGNET_TEST -> {
                    WizardStepCard(
                        title = "Step 1: Magnet Test",
                        description = "Does a strong magnet stick to the metal?",
                        options = listOf(
                            "Yes, it sticks strongly" to { viewModel.answerMagnet(true) },
                            "No, it does not stick" to { viewModel.answerMagnet(false) }
                        )
                    )
                }
                WizardStep.COLOR_TEST -> {
                    WizardStepCard(
                        title = "Step 2: Color & Tarnish",
                        description = "What is the primary color of the metal underneath any paint or dirt? (Scratch it if needed)",
                        options = listOf(
                            "Red / Orange" to { viewModel.answerColor("Red/Orange") },
                            "Yellow" to { viewModel.answerColor("Yellow") },
                            "Silver / Grey" to { viewModel.answerColor("Silver/Grey") }
                        )
                    )
                }
                WizardStep.DENSITY_TEST -> {
                    WizardStepCard(
                        title = "Step 3: Weight / Density",
                        description = "Is it surprisingly heavy for its size, or relatively light?",
                        options = listOf(
                            "Heavy (Dense)" to { viewModel.answerDensity(true) },
                            "Light (Aluminum-like)" to { viewModel.answerDensity(false) }
                        )
                    )
                }
                WizardStep.ACID_TEST -> {
                    WizardStepCard(
                        title = "Step 3: Controlled Acid Test",
                        description = "Only with PPE and a commercial metal test kit, test a cleaned hidden spot. Does the kit indicate an active zinc/galvanized reaction? Never mix acids or test unknown sealed containers.",
                        options = listOf(
                            "Yes — zinc/galvanized reaction" to { viewModel.answerAcid(true) },
                            "No clear reaction" to { viewModel.answerAcid(false) }
                        )
                    )
                }
                WizardStep.SPARK_TEST -> {
                    WizardStepCard(
                        title = "Step 4: Spark Test (Grinder)",
                        description = "When touched with an angle grinder, what do the sparks look like?",
                        options = listOf(
                            "Long, bright, branching sparks" to { viewModel.answerSpark(true) },
                            "Short, dull, or red sparks" to { viewModel.answerSpark(false) }
                        )
                    )
                }
                WizardStep.RESULT -> {
                    OutlinedCard(
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Copper),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                            Text("Identified Metal", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(result, color = IndustrialOrange, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    classification, 
                                    color = MaterialTheme.colorScheme.onSurface, 
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.reset() }, 
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface)
                    ) { 
                        Text("Start Over") 
                    }
                }
            }
            } // Close inner padding column
        }
    }
}

@Composable
fun WizardStepCard(title: String, description: String, options: List<Pair<String, () -> Unit>>) {
    OutlinedCard(
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = IndustrialOrange)
            Spacer(modifier = Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(24.dp))
            
            options.forEach { (text, onClick) ->
                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(text, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}
