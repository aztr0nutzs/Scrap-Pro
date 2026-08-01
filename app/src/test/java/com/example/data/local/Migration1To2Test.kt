package com.example.data.local

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Migration1To2Test {
    @Test
    fun migration1To2PreservesExistingTripsAndCreatesActiveHaulTables() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "migration-1-2-${System.nanoTime()}.db"
        val configuration = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(name)
            .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL("CREATE TABLE IF NOT EXISTS `yards` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `yardName` TEXT NOT NULL, `address` TEXT NOT NULL, `distanceMiles` REAL NOT NULL, `phone` TEXT NOT NULL, `notes` TEXT NOT NULL)")
                    db.execSQL("CREATE TABLE IF NOT EXISTS `yard_prices` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `yardId` INTEGER NOT NULL, `metalGrade` TEXT NOT NULL, `pricePerLb` REAL NOT NULL, `pricePerTon` REAL NOT NULL, `lastUpdatedTimestamp` INTEGER NOT NULL, FOREIGN KEY(`yardId`) REFERENCES `yards`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)")
                    db.execSQL("CREATE INDEX IF NOT EXISTS `index_yard_prices_yardId` ON `yard_prices` (`yardId`)")
                    db.execSQL("CREATE TABLE IF NOT EXISTS `trip_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` INTEGER NOT NULL, `yardId` INTEGER, `totalGrossPayout` REAL NOT NULL, `totalExpenses` REAL NOT NULL, `netProfit` REAL NOT NULL, `totalWeightLbs` REAL NOT NULL, `receiptUri` TEXT, `notes` TEXT NOT NULL, FOREIGN KEY(`yardId`) REFERENCES `yards`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL)")
                    db.execSQL("CREATE INDEX IF NOT EXISTS `index_trip_logs_yardId` ON `trip_logs` (`yardId`)")
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
            })
            .build()
        FrameworkSQLiteOpenHelperFactory().create(configuration).use { helper ->
            helper.writableDatabase.execSQL("INSERT INTO trip_logs (date, yardId, totalGrossPayout, totalExpenses, netProfit, totalWeightLbs, receiptUri, notes) VALUES (1000, NULL, 20.0, 5.0, 15.0, 10.0, NULL, 'existing')")
        }

        val database = Room.databaseBuilder(context, ScrapProDatabase::class.java, name)
            .addMigrations(ScrapProDatabase.MIGRATION_1_2, ScrapProDatabase.MIGRATION_2_3, ScrapProDatabase.MIGRATION_3_4)
            .allowMainThreadQueries()
            .build()
        database.openHelper.writableDatabase

        val cursor = database.openHelper.readableDatabase.query("SELECT COUNT(*) FROM trip_logs")
        cursor.use {
            it.moveToFirst()
            assertEquals(1, it.getInt(0))
        }
        database.openHelper.readableDatabase.query("SELECT COUNT(*) FROM active_haul").close()
        database.openHelper.readableDatabase.query("SELECT COUNT(*) FROM active_haul_items").close()
        database.close()
        context.deleteDatabase(name)
    }
}
