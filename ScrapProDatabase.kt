package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.TripLogDao
import com.example.data.local.dao.YardDao
import com.example.data.local.entity.TripLogEntity
import com.example.data.local.entity.YardEntity
import com.example.data.local.entity.YardPriceEntity

@Database(
    entities = [YardEntity::class, YardPriceEntity::class, TripLogEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ScrapProDatabase : RoomDatabase() {
    abstract fun yardDao(): YardDao
    abstract fun tripLogDao(): TripLogDao

    companion object {
        @Volatile
        private var Instance: ScrapProDatabase? = null

        fun getDatabase(context: Context): ScrapProDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    ScrapProDatabase::class.java,
                    "scrappro_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                .also { Instance = it }
            }
        }
    }
}
