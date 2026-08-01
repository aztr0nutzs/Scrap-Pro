package com.example.domain.model


data class ActiveHaul(
    val items: List<ScrapItem>,
    val expense: ProcessingExpense,
    val totalWeightLbs: Double,
    val estimatedGrossPayout: Double,
    val expenses: Double,
    val laborDeduction: Double,
    val estimatedNetProfit: Double,
    val createdAt: Long,
    val updatedAt: Long
)

data class RecentHaulActivity(
    val id: Long,
    val completedAt: Long,
    val yardName: String?,
    val totalWeightLbs: Double,
    val grossPayout: Double,
    val expenses: Double,
    val netProfit: Double
)
