package com.example.ui.core

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.R
import com.example.domain.model.MetalGrade
import com.example.ui.theme.Copper
import com.example.ui.theme.IndustrialOrange
import com.example.ui.theme.IndustrialBlue
import com.example.ui.theme.SafetyYellow

data class MetalAssetProperties(
    @DrawableRes val drawableRes: Int,
    val isFerrous: Boolean,
    val primaryColor: Color,
    val description: String
)

object MetalAssetRegistry {
    fun getPropertiesForGrade(grade: MetalGrade): MetalAssetProperties {
        return when (grade) {
            MetalGrade.BARE_BRIGHT_COPPER,
            MetalGrade.NUMBER_1_COPPER,
            MetalGrade.NUMBER_2_COPPER -> MetalAssetProperties(
                drawableRes = R.drawable.scrap_icon,
                isFerrous = false,
                primaryColor = Copper,
                description = "Excellent conductor of heat and electricity"
            )
            MetalGrade.BRASS -> MetalAssetProperties(
                drawableRes = R.drawable.scrap_icon,
                isFerrous = false,
                primaryColor = SafetyYellow,
                description = "Corrosion resistant and easy to machine"
            )
            MetalGrade.CAST_ALUMINUM,
            MetalGrade.SHEET_ALUMINUM -> MetalAssetProperties(
                drawableRes = R.drawable.scrap_icon,
                isFerrous = false,
                primaryColor = Color(0xFFA0AAB2),
                description = "Lightweight, corrosion resistant, versatile"
            )
            MetalGrade.STAINLESS_STEEL -> MetalAssetProperties(
                drawableRes = R.drawable.scrap_icon,
                isFerrous = true, // Technically some stainless is non-magnetic, but usually grouped with steel or separate. We'll mark it non-ferrous in practice at yard, but wait - the prompt says steel is strong durable. Let's just map it to steel.
                primaryColor = IndustrialBlue,
                description = "Strong, durable, construction grade"
            )
            MetalGrade.HEAVY_MELTING_STEEL -> MetalAssetProperties(
                drawableRes = R.drawable.scrap_icon,
                isFerrous = true,
                primaryColor = IndustrialOrange,
                description = "Strong and dense, essential for industrial applications"
            )
        }
    }
}
