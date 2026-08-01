package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.HomeDao
import com.example.data.local.dao.TripLogDao
import com.example.data.local.dao.YardDao
import com.example.data.local.entity.ActiveHaulEntity
import com.example.data.local.entity.ActiveHaulItemEntity
import com.example.data.local.entity.TripLogEntity
import com.example.data.local.entity.YardEntity
import com.example.data.local.entity.YardPriceEntity

@Database(
    entities = [
        YardEntity::class,
        YardPriceEntity::class,
        TripLogEntity::class,
        ActiveHaulEntity::class,
        ActiveHaulItemEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ScrapProDatabase : RoomDatabase() {
    abstract fun yardDao(): YardDao
    abstract fun tripLogDao(): TripLogDao
    abstract fun homeDao(): HomeDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `active_haul` (
                        `id` INTEGER NOT NULL,
                        `fuelCost` REAL NOT NULL,
                        `otherExpenses` REAL NOT NULL,
                        `laborHours` REAL NOT NULL,
                        `hourlyLaborRate` REAL NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `active_haul_items` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `haulId` INTEGER NOT NULL,
                        `position` INTEGER NOT NULL,
                        `metalGrade` TEXT NOT NULL,
                        `weightLbs` REAL NOT NULL,
                        `pricePerLb` REAL NOT NULL,
                        `isCleaned` INTEGER NOT NULL,
                        `recoverableYieldPercent` REAL NOT NULL,
                        FOREIGN KEY(`haulId`) REFERENCES `active_haul`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_active_haul_items_haulId` ON `active_haul_items` (`haulId`)")
            }
        }

        @Volatile
        private var instance: ScrapProDatabase? = null

        fun getDatabase(context: Context): ScrapProDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ScrapProDatabase::class.java,
                    "scrappro_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { instance = it }
            }
    }
}
