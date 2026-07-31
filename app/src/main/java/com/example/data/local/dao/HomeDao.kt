package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.example.data.local.entity.ActiveHaulEntity
import com.example.data.local.entity.ActiveHaulItemEntity
import com.example.data.local.entity.TripLogEntity
import kotlinx.coroutines.flow.Flow


data class ActiveHaulWithItems(
    @Embedded val haul: ActiveHaulEntity,
    @Relation(parentColumn = "id", entityColumn = "haulId")
    val items: List<ActiveHaulItemEntity>
)

data class RecentHaulActivityRow(
    val id: Long,
    val completedAt: Long,
    val yardName: String?,
    val totalWeightLbs: Double,
    val grossPayout: Double,
    val expenses: Double,
    val netProfit: Double
)

@Dao
abstract class HomeDao {
    @Transaction
    @Query("SELECT * FROM active_haul WHERE id = 1")
    abstract fun observeActiveHaul(): Flow<ActiveHaulWithItems?>

    @Transaction
    @Query("SELECT * FROM active_haul WHERE id = 1")
    abstract suspend fun getActiveHaul(): ActiveHaulWithItems?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun upsertHaul(haul: ActiveHaulEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertItems(items: List<ActiveHaulItemEntity>)

    @Query("DELETE FROM active_haul_items WHERE haulId = 1")
    abstract suspend fun deleteItems()

    @Query("DELETE FROM active_haul WHERE id = 1")
    abstract suspend fun deleteHaul()

    @Insert
    abstract suspend fun insertTripLog(tripLog: TripLogEntity): Long

    @Query(
        """
        SELECT trip_logs.id AS id,
               trip_logs.date AS completedAt,
               yards.yardName AS yardName,
               trip_logs.totalWeightLbs AS totalWeightLbs,
               trip_logs.totalGrossPayout AS grossPayout,
               trip_logs.totalExpenses AS expenses,
               trip_logs.netProfit AS netProfit
        FROM trip_logs
        LEFT JOIN yards ON yards.id = trip_logs.yardId
        ORDER BY trip_logs.date DESC, trip_logs.id DESC
        LIMIT :limit
        """
    )
    abstract fun observeRecentActivity(limit: Int): Flow<List<RecentHaulActivityRow>>

    @Transaction
    open suspend fun replaceActiveHaul(haul: ActiveHaulEntity, items: List<ActiveHaulItemEntity>) {
        upsertHaul(haul)
        deleteItems()
        if (items.isNotEmpty()) insertItems(items)
    }

    @Transaction
    open suspend fun completeHaul(tripLog: TripLogEntity) {
        insertTripLog(tripLog)
        deleteHaul()
    }

    @Transaction
    open suspend fun clearActiveHaul() {
        deleteHaul()
    }
}
