package com.example.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ScrapProDatabase
import com.example.domain.model.TowSafetyInput
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TowProfileRepositoryTest {
 private lateinit var db:ScrapProDatabase; private lateinit var repo:TowProfileRepository
 @Before fun setup(){db=Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext<Context>(),ScrapProDatabase::class.java).allowMainThreadQueries().build();repo=TowProfileRepository(db.towProfileDao()){1000}}
 @After fun close(){db.close()}
 @Test fun currentAndNamedProfilesRestoreAllFields()=runTest { val input=TowSafetyInput(vehicleGvwr=7000.0,trailerGvwr=5000.0,truckCargoPosition=-0.5,safetyMarginPercent=10.0);repo.saveCurrent(input);repo.saveNamed("Work rig",input);assertEquals(input,repo.observeCurrent().first());assertEquals(input,repo.observeNamed().first().single().input) }
}
