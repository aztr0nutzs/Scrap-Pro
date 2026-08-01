package com.example.ui.yardfinder

import android.content.Intent
import com.example.data.domain.ScrapYardLocation
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class YardIntentTest {
    private val yard = ScrapYardLocation(name="A&B", address="x", latitude=1.0, longitude=2.0, phoneNumber="", operatingHours="", temporarilyClosed=false, acceptsFerrous=false, acceptsNonFerrous=false, acceptsVehicles=false, acceptsEWaste=false, cashPayout=false, checkPayout=false, digitalPayout=false, requiredId="", hasTruckScale=false, notes="", favorite=false, bundledStarter=false)
    @Test fun dialIntentValidatesPhone() { assertNull(buildDialIntent("555")); assertEquals(Intent.ACTION_DIAL, buildDialIntent("+1 (212) 555-1212")!!.action) }
    @Test fun mapsIntentHasNativeAndBrowserFallback() { val intents=buildMapIntents(yard); assertEquals("com.google.android.apps.maps", intents[0].getPackage()); assertTrue(intents[0].dataString!!.startsWith("geo:0,0?q=1.0,2.0")); assertTrue(intents[1].dataString!!.startsWith("https://")) }
}
