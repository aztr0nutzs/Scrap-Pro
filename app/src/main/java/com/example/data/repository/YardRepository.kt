package com.example.data.repository

import com.example.data.domain.ScrapYardLocation
import com.example.data.domain.YardPrice
import com.example.data.local.dao.YardDao
import com.example.data.local.entity.YardEntity
import com.example.data.local.entity.YardPriceEntity
import com.example.domain.model.MetalGrade
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class YardRepository(private val dao: YardDao) {
    fun observeYards(): Flow<List<ScrapYardLocation>> = dao.observeYards().map { rows -> rows.map { it.toDomain() } }
    fun getAllYards(): Flow<List<YardEntity>> = dao.observeYards()
    fun getPricesForMetal(metalGrade: MetalGrade): Flow<List<YardPriceEntity>> = dao.observePricesForMetal(metalGrade)
    fun observePrices(): Flow<Map<Long, List<YardPrice>>> = dao.observePrices().map { rows ->
        rows.map { it.toDomain() }.groupBy { it.yardId }
    }

    suspend fun saveYard(yard: ScrapYardLocation): Long = dao.upsertYard(yard.toEntity())
    suspend fun deleteYard(yard: ScrapYardLocation) = dao.deleteYard(yard.toEntity())
    suspend fun toggleFavorite(yard: ScrapYardLocation) = saveYard(yard.copy(favorite = !yard.favorite))

    suspend fun savePrice(price: YardPrice) {
        val grade = runCatching { MetalGrade.valueOf(price.metalGrade) }.getOrDefault(MetalGrade.HEAVY_MELTING_STEEL)
        dao.upsertPrices(listOf(YardPriceEntity(
            id = price.id, yardId = price.yardId, metalGrade = grade,
            pricePerLb = if (price.unit == "lb") price.price else 0.0,
            pricePerTon = if (price.unit == "ton") price.price else 0.0,
            lastUpdatedTimestamp = price.observedAt, price = price.price, unit = price.unit
        )))
    }

    suspend fun deletePrice(price: YardPrice) {
        val grade = runCatching { MetalGrade.valueOf(price.metalGrade) }.getOrDefault(MetalGrade.HEAVY_MELTING_STEEL)
        dao.deletePrice(YardPriceEntity(price.id, price.yardId, grade, lastUpdatedTimestamp = price.observedAt, price = price.price, unit = price.unit))
    }

    suspend fun importStarterDataset() {
        if (dao.starterCount() > 0) return
        listOf("Bundled starter yard A", "Bundled starter yard B").forEach { name ->
            dao.upsertYard(YardEntity(
                yardName = name,
                address = "Starter record — edit with a verified local address",
                phone = "",
                notes = "Bundled template only; not a verified live listing or market source.",
                bundledStarter = true
            ))
        }
    }

    private fun YardEntity.toDomain() = ScrapYardLocation(
        id, yardName, address, latitude, longitude, phone, operatingHours, temporarilyClosed,
        acceptsFerrous, acceptsNonFerrous, acceptsVehicles, acceptsEWaste,
        cashPayout, checkPayout, digitalPayout, requiredId, hasTruckScale, notes, favorite, bundledStarter
    )

    private fun ScrapYardLocation.toEntity() = YardEntity(
        id, name, address, distanceMiles ?: 0.0, phoneNumber, notes, latitude, longitude,
        operatingHours, temporarilyClosed, acceptsFerrous, acceptsNonFerrous, acceptsVehicles,
        acceptsEWaste, cashPayout, checkPayout, digitalPayout, requiredId, hasTruckScale, favorite, bundledStarter
    )

    private fun YardPriceEntity.toDomain() = YardPrice(id, yardId, metalGrade.name, price, unit, lastUpdatedTimestamp)
}
