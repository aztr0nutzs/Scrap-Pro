package com.example.ui.core

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.model.MetalGrade

@Composable
fun MetalSelectorCard(
    grade: MetalGrade,
    pricePerLb: Double,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val properties = MetalAssetRegistry.getPropertiesForGrade(grade)
    
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        elevation = CardDefaults.outlinedCardElevation(defaultElevation = if (selected) 8.dp else 4.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (selected) properties.primaryColor.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (selected) 2.dp else 1.dp,
            if (selected) properties.primaryColor else MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = properties.drawableRes),
                contentDescription = grade.displayName,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(2.dp, properties.primaryColor, CircleShape),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = grade.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = properties.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BadgeChip(
                        text = if (properties.isFerrous) "Ferrous" else "Non-Ferrous",
                        color = if (properties.isFerrous) Color(0xFFEF4444) else Color(0xFF10B981)
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = properties.primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = "$${"%.2f".format(pricePerLb)}/lb",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = properties.primaryColor
                )
            }
        }
    }
}

@Composable
fun MetalCategoryChip(
    grade: MetalGrade,
    selected: Boolean,
    onClick: () -> Unit
) {
    val properties = MetalAssetRegistry.getPropertiesForGrade(grade)
    
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) properties.primaryColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) properties.primaryColor else Color.Transparent
        ),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = properties.drawableRes),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = grade.displayName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (selected) properties.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MetalHeaderBanner(grade: MetalGrade) {
    val properties = MetalAssetRegistry.getPropertiesForGrade(grade)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = properties.primaryColor.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, properties.primaryColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            BadgeChip(
                text = if (properties.isFerrous) "Ferrous - Magnetic" else "Non-Ferrous - Not Magnetic",
                color = if (properties.isFerrous) Color(0xFFEF4444) else Color(0xFF10B981)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = grade.displayName,
                style = MaterialTheme.typography.headlineLarge,
                color = properties.primaryColor
            )
            Text(
                text = properties.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BadgeChip(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
