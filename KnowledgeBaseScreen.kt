package com.example.ui.knowledge

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.KnowledgeBaseData
import com.example.ui.theme.IndustrialOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeBaseScreen() {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { 
            TopAppBar(
                title = { Text("Field Knowledge Base") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            ) 
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Metal Profiles", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = IndustrialOrange)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            items(com.example.domain.model.MetalGrade.values().filter { 
                it in listOf(
                    com.example.domain.model.MetalGrade.BARE_BRIGHT_COPPER,
                    com.example.domain.model.MetalGrade.BRASS,
                    com.example.domain.model.MetalGrade.CAST_ALUMINUM,
                    com.example.domain.model.MetalGrade.STAINLESS_STEEL,
                    com.example.domain.model.MetalGrade.HEAVY_MELTING_STEEL
                )
            }) { grade ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    com.example.ui.core.MetalHeaderBanner(grade = grade)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Field Manual & Strategy", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = IndustrialOrange)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            items(KnowledgeBaseData.sections) { section ->
                var expanded by remember { mutableStateOf(false) }
                
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (expanded) IndustrialOrange else MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = section.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (expanded) IndustrialOrange else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (expanded) "Collapse" else "Expand",
                                tint = if (expanded) IndustrialOrange else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        AnimatedVisibility(
                            visible = expanded,
                            enter = expandVertically(animationSpec = tween(300)),
                            exit = shrinkVertically(animationSpec = tween(300))
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(12.dp))
                                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = section.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
