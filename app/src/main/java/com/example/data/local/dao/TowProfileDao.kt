package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.TowProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TowProfileDao {
 @Query("SELECT * FROM tow_profiles ORDER BY CASE WHEN name = '__CURRENT__' THEN 0 ELSE 1 END, name") fun observeAll(): Flow<List<TowProfileEntity>>
 @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun upsert(profile: TowProfileEntity): Long
 @Query("DELETE FROM tow_profiles WHERE id=:id AND name != '__CURRENT__'") suspend fun delete(id:Long)
}
