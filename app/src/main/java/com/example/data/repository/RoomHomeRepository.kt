package com.example.data.repository

import com.example.data.local.dao.ActiveHaulWithItems
import com.example.data.local.dao.HomeDao
import com.example.data.local.entity.ActiveHaulEntity
import com.example.data.local.entity.ActiveHaulItemEntity
import com.example.data.local.entity.TripLogEntity
import com.example.domain.engine.ProfitCalculationEngine
import com.example.domain.model.ActiveHaul
import com.example.domain.model.LoadItem
import com.example.domain.model.ProcessingExpense
import com.example.domain.model.RecentHaulActivity
import com.example.domain.model.ScrapItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomHomeRepository(
    private val homeDao: HomeDao,
    private val clock: () -> Long = System::currentTimeMillis,
    private val engine: ProfitCalculationEngine = ProfitCalculationEngine()
) : HomeRepository {
    override fun observeActiveHaul(): Flow<ActiveHaul?> =
        homeDao.observeActiveHaul().map { relation -> relation?.toDomain() }

    override fun observeRecentActivity(limit: Int): Flow<List<RecentHaulActivity>> =
        homeDao.observeRecentActivity(limit).map { rows ->
            rows.map { row ->
                RecentHaulActivity(
                    id = row.id,
                    completedAt = row.completedAt,
                    yardName = row.yardName,
                    totalWeightLbs = row.totalWeightLbs,
                    grossPayout = row.grossPayout,
                    expenses = row.expenses,
                    netProfit = row.netProfit
                )
            }
        }

    override suspend fun startHaul() {
        if (homeDao.getActiveHaul() != null) return
        val now = clock()
        homeDao.replaceActiveHaul(
            ActiveHaulEntity(
                fuelCost = 0.0,
                otherExpenses = 0.0,
                laborHours = 0.0,
                hourlyLaborRate = 25.0,
                createdAt = now,
                updatedAt = now
            ),
            emptyList()
        )
    }

    override suspend fun saveActiveHaul(items: List<ScrapItem>, expense: ProcessingExpense) {
        val existing = homeDao.getActiveHaul()
        val now = clock()
        val safeItems = items.map { item ->
            item.copy(
                weightLbs = item.weightLbs.coerceAtLeast(0.0),
                pricePerLb = item.pricePerLb.coerceAtLeast(0.0),
                recoverableYieldPercent = item.recoverableYieldPercent.coerceIn(0.0, 100.0)
            )
        }
        val safeExpense = expense.copy(
            fuelCost = expense.fuelCost.coerceAtLeast(0.0),
            tolls = expense.tolls.coerceAtLeast(0.0),
            processingLaborHours = expense.processingLaborHours.coerceAtLeast(0.0),
            hourlyLaborRate = expense.hourlyLaborRate.coerceAtLeast(0.0)
        )
        val haul = ActiveHaulEntity(
            fuelCost = safeExpense.fuelCost,
            otherExpenses = safeExpense.tolls,
            laborHours = safeExpense.processingLaborHours,
            hourlyLaborRate = safeExpense.hourlyLaborRate,
            createdAt = existing?.haul?.createdAt ?: now,
            updatedAt = now
        )
        val entities = safeItems.mapIndexed { index, item ->
            ActiveHaulItemEntity(
                position = index,
                metalGrade = item.grade,
                weightLbs = item.weightLbs,
                pricePerLb = item.pricePerLb,
                isCleaned = item.isCleaned,
                recoverableYieldPercent = item.recoverableYieldPercent
            )
        }
        homeDao.replaceActiveHaul(haul, entities)
    }

    override suspend fun completeActiveHaul() {
        val relation = homeDao.getActiveHaul() ?: return
        val haul = relation.toDomain()
        homeDao.completeHaul(
            TripLogEntity(
                date = clock(),
                yardId = null,
                totalGrossPayout = haul.estimatedGrossPayout,
                totalExpenses = haul.expenses + haul.laborDeduction,
                netProfit = haul.estimatedNetProfit,
                totalWeightLbs = haul.totalWeightLbs,
                receiptUri = null,
                notes = "Completed active haul"
            )
        )
    }

    override suspend fun clearActiveHaul() = homeDao.clearActiveHaul()

    private fun ActiveHaulWithItems.toDomain(): ActiveHaul {
        val orderedItems = items.sortedBy { it.position }.map { item ->
            ScrapItem(
                grade = item.metalGrade,
                weightLbs = item.weightLbs,
                pricePerLb = item.pricePerLb,
                isCleaned = item.isCleaned,
                recoverableYieldPercent = item.recoverableYieldPercent
            )
        }
        val expense = ProcessingExpense(
            fuelCost = haul.fuelCost,
            tolls = haul.otherExpenses,
            processingLaborHours = haul.laborHours,
            hourlyLaborRate = haul.hourlyLaborRate
        )
        val result = engine.calculateProfit(LoadItem(orderedItems, expense))
        return ActiveHaul(
            items = orderedItems,
            expense = expense,
            totalWeightLbs = orderedItems.sumOf { it.weightLbs.coerceAtLeast(0.0) },
            estimatedGrossPayout = result.grossPayout,
            expenses = expense.fuelCost + expense.tolls,
            laborDeduction = expense.processingLaborHours * expense.hourlyLaborRate,
            estimatedNetProfit = result.netProfit,
            createdAt = haul.createdAt,
            updatedAt = haul.updatedAt
        )
    }
}
