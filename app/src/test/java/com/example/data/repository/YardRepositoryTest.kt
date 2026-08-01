package com.example.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.domain.*
import com.example.data.local.ScrapProDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class YardRepositoryTest {
    private lateinit var db: ScrapProDatabase; private lateinit var repo: YardRepository
    @Before fun setup(){ db=Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext<Context>(), ScrapProDatabase::class.java).allowMainThreadQueries().build(); repo=YardRepository(db.yardDao()) }
    @After fun close(){ db.close() }
    @Test fun emptyCrudFavoriteAndPriceHistory()=runTest {
        assertTrue(repo.observeYards().first().isEmpty())
        val id=repo.saveYard(ScrapYardLocation(name="Verified",address="1 Main",latitude=1.0,longitude=2.0,phoneNumber="2125551212",operatingHours="",temporarilyClosed=false,acceptsFerrous=true,acceptsNonFerrous=true,acceptsVehicles=false,acceptsEWaste=false,cashPayout=true,checkPayout=false,digitalPayout=false,requiredId="ID",hasTruckScale=true,notes="",favorite=false,bundledStarter=false))
        val yard=repo.observeYards().first().single(); assertEquals(id,yard.id); repo.toggleFavorite(yard); assertTrue(repo.observeYards().first().single().favorite)
        repo.savePrice(YardPrice(yardId=id,metalGrade="BRASS",price=2.25,unit="lb",observedAt=1000)); assertEquals(2.25,repo.observePrices().first()[id]!!.single().price,0.0)
        repo.deleteYard(repo.observeYards().first().single()); assertTrue(repo.observeYards().first().isEmpty()); assertTrue(repo.observePrices().first().isEmpty())
    }
}
