package com.example.ui.outreach

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Copper
import com.example.ui.theme.IndustrialOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractorOutreachScreen() {
    var scrapperName by remember { mutableStateOf("") }
    var targetAudience by remember { mutableStateOf("Plumbers") }
    var pickupSchedule by remember { mutableStateOf("Weekly") }
    var valueProposition by remember { mutableStateOf("Free Clean Up") }
    
    var expandedAudience by remember { mutableStateOf(false) }
    var expandedSchedule by remember { mutableStateOf(false) }
    var expandedValue by remember { mutableStateOf(false) }
    
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val audiences = listOf("Plumbers", "Electricians", "HVAC Techs", "Construction Foremen")
    val schedules = listOf("Weekly", "Bi-Weekly", "On-Call / As Needed")
    val valueProps = listOf("Free Clean Up", "50/50 Profit Split", "Flat Fee Removal")

    val generatedMessage = """
        Hi there! My name is $scrapperName. I'm a local scrapper and I know ${targetAudience} generate a lot of scrap metal.
        
        I offer a $pickupSchedule pickup service to keep your job sites and shop clean. My offer: $valueProposition.
        
        Let me know if you have a pile you need gone soon!
    """.trimIndent()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { 
            TopAppBar(
                title = { Text("Contractor Outreach") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            ) 
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState())) {
            
            Text("Outreach Script Generator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = scrapperName,
                onValueChange = { scrapperName = it },
                label = { Text("Your Name / Company") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Target Audience Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedAudience,
                onExpandedChange = { expandedAudience = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = targetAudience,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Target Audience") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAudience) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedAudience,
                    onDismissRequest = { expandedAudience = false }
                ) {
                    audiences.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = { targetAudience = option; expandedAudience = false }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Pickup Schedule Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedSchedule,
                onExpandedChange = { expandedSchedule = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = pickupSchedule,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pickup Schedule") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSchedule) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedSchedule,
                    onDismissRequest = { expandedSchedule = false }
                ) {
                    schedules.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = { pickupSchedule = option; expandedSchedule = false }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Value Proposition Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedValue,
                onExpandedChange = { expandedValue = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = valueProposition,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Value Proposition") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedValue) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedValue,
                    onDismissRequest = { expandedValue = false }
                ) {
                    valueProps.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = { valueProposition = option; expandedValue = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedCard(
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Generated Message", style = MaterialTheme.typography.titleSmall, color = IndustrialOrange)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(generatedMessage, style = MaterialTheme.typography.bodyMedium)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { clipboardManager.setText(AnnotatedString(generatedMessage)) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copy")
                }
                
                Button(
                    onClick = { sendSMS(context, generatedMessage) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Copper)
                ) {
                    Icon(Icons.Default.Sms, contentDescription = "SMS", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SMS")
                }
                
                Button(
                    onClick = { sendEmail(context, generatedMessage) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = IndustrialOrange)
                ) {
                    Icon(Icons.Default.Email, contentDescription = "Email", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Email")
                }
            }
        }
    }
}

fun sendSMS(context: Context, message: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse("sms:")
    intent.putExtra("sms_body", message)
    context.startActivity(intent)
}

fun sendEmail(context: Context, message: String) {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:")
    intent.putExtra(Intent.EXTRA_SUBJECT, "Scrap Metal Pickup")
    intent.putExtra(Intent.EXTRA_TEXT, message)
    context.startActivity(intent)
}
