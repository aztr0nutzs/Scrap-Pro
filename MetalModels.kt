package com.example.domain.model

enum class MetalGrade(val displayName: String, val defaultPricePerLb: Double) {
    BARE_BRIGHT_COPPER("Bare Bright Copper", 3.80),
    NUMBER_1_COPPER("#1 Copper", 3.60),
    NUMBER_2_COPPER("#2 Copper", 3.30),
    BRASS("Brass", 2.10),
    CAST_ALUMINUM("Cast Aluminum", 0.45),
    SHEET_ALUMINUM("Sheet Aluminum", 0.50),
    STAINLESS_STEEL("Stainless Steel", 0.40),
    HEAVY_MELTING_STEEL("Heavy Melting Steel", 0.08)
}

data class ScrapItem(
    val grade: MetalGrade,
    val weightLbs: Double,
    val pricePerLb: Double = grade.defaultPricePerLb,
    val isCleaned: Boolean = true,
    val recoverableYieldPercent: Double = 100.0
)

data class ProcessingExpense(
    val fuelCost: Double = 0.0,
    val tolls: Double = 0.0,
    val processingLaborHours: Double = 0.0,
    val hourlyLaborRate: Double = 25.0
) {
    val totalExpense: Double get() = fuelCost + tolls + (processingLaborHours * hourlyLaborRate)
}

data class LoadItem(
    val items: List<ScrapItem>,
    val expense: ProcessingExpense
)

enum class WireCategory(val displayName: String, val recoveryYieldPercentage: Double) {
    THHN("THHN (Solid)", 0.80),
    ROMEX("Romex (House Wire)", 0.65),
    HIGH_RECOVERY("High-Recovery (Thick Insulation)", 0.85),
    LOW_RECOVERY("Low-Recovery (Thin/Data Wire)", 0.35)
}
