package com.example.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ScrapProDatabase
import com.example.domain.model.MetalGrade
import com.example.domain.model.ProcessingExpense
import com.example.domain.model.ScrapItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RoomHomeRepositoryTest {
    private lateinit var database: ScrapProDatabase
    private var now = 1_000L
    private lateinit var repository: RoomHomeRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ScrapProDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = RoomHomeRepository(database.homeDao(), clock = { now })
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun emptyDatabaseHasNoActiveHaulOrRecentActivity() = runTest {
        assertNull(repository.observeActiveHaul().first())
        assertEquals(emptyList<Any>(), repository.observeRecentActivity().first())
    }

    @Test
    fun startHaulCreatesPersistedEmptyHaul() = runTest {
        repository.startHaul()

        val haul = repository.observeActiveHaul().first()!!
        assertEquals(0, haul.items.size)
        assertEquals(1_000L, haul.createdAt)
        assertEquals(0.0, haul.estimatedNetProfit, 0.001)
    }

    @Test
    fun multipleMetalsExpensesAndLaborProduceRepositoryTotals() = runTest {
        repository.saveActiveHaul(
            items = listOf(
                ScrapItem(MetalGrade.BRASS, 10.0, 2.0),
                ScrapItem(MetalGrade.HEAVY_MELTING_STEEL, 100.0, 0.1)
            ),
            expense = ProcessingExpense(
                fuelCost = 5.0,
                tolls = 3.0,
                processingLaborHours = 2.0,
                hourlyLaborRate = 10.0
            )
        )

        val haul = repository.observeActiveHaul().first()!!
        assertEquals(110.0, haul.totalWeightLbs, 0.001)
        assertEquals(30.0, haul.estimatedGrossPayout, 0.001)
        assertEquals(8.0, haul.expenses, 0.001)
        assertEquals(20.0, haul.laborDeduction, 0.001)
        assertEquals(2.0, haul.estimatedNetProfit, 0.001)
    }

    @Test
    fun savedHaulRestoresThroughNewRepositoryInstance() = runTest {
        repository.saveActiveHaul(
            listOf(ScrapItem(MetalGrade.NUMBER_1_COPPER, 4.0, 3.5)),
            ProcessingExpense(fuelCost = 2.0)
        )

        val restored = RoomHomeRepository(database.homeDao(), clock = { now })
            .observeActiveHaul().first()!!
        assertEquals(MetalGrade.NUMBER_1_COPPER, restored.items.single().grade)
        assertEquals(4.0, restored.items.single().weightLbs, 0.001)
        assertEquals(12.0, restored.estimatedNetProfit, 0.001)
    }

    @Test
    fun completingHaulClearsActiveStateAndCreatesActivity() = runTest {
        repository.saveActiveHaul(
            listOf(ScrapItem(MetalGrade.BARE_BRIGHT_COPPER, 2.0, 4.0)),
            ProcessingExpense(fuelCost = 1.0)
        )
        now = 2_000L

        repository.completeActiveHaul()

        assertNull(repository.observeActiveHaul().first())
        val activity = repository.observeRecentActivity().first().single()
        assertEquals(2_000L, activity.completedAt)
        assertEquals(2.0, activity.totalWeightLbs, 0.001)
        assertEquals(7.0, activity.netProfit, 0.001)
    }

    @Test
    fun recentActivityIsNewestFirst() = runTest {
        repository.saveActiveHaul(
            listOf(ScrapItem(MetalGrade.BRASS, 1.0, 2.0)),
            ProcessingExpense()
        )
        now = 2_000L
        repository.completeActiveHaul()
        now = 3_000L
        repository.saveActiveHaul(
            listOf(ScrapItem(MetalGrade.BRASS, 2.0, 2.0)),
            ProcessingExpense()
        )
        now = 4_000L
        repository.completeActiveHaul()

        assertEquals(listOf(4_000L, 2_000L), repository.observeRecentActivity().first().map { it.completedAt })
    }
}
