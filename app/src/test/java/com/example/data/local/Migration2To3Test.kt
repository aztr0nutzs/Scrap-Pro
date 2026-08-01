package com.example.data.local

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Migration2To3Test {
 @Test fun migrationAddsDirectoryAndPriceColumns(){
  val context=ApplicationProvider.getApplicationContext<Context>(); val name="m23-${System.nanoTime()}.db"
  val config=SupportSQLiteOpenHelper.Configuration.builder(context).name(name).callback(object:SupportSQLiteOpenHelper.Callback(2){
   override fun onCreate(db:SupportSQLiteDatabase){ db.execSQL("CREATE TABLE yards (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, yardName TEXT NOT NULL, address TEXT NOT NULL, distanceMiles REAL NOT NULL, phone TEXT NOT NULL, notes TEXT NOT NULL)"); db.execSQL("CREATE TABLE yard_prices (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, yardId INTEGER NOT NULL, metalGrade TEXT NOT NULL, pricePerLb REAL NOT NULL, pricePerTon REAL NOT NULL, lastUpdatedTimestamp INTEGER NOT NULL)") }
   override fun onUpgrade(db:SupportSQLiteDatabase,oldVersion:Int,newVersion:Int)=Unit
  }).build()
  FrameworkSQLiteOpenHelperFactory().create(config).use { helper -> val db=helper.writableDatabase; ScrapProDatabase.MIGRATION_2_3.migrate(db); val yardCols=db.query("PRAGMA table_info(yards)").use { c -> buildSet { while(c.moveToNext()) add(c.getString(1)) } }; val priceCols=db.query("PRAGMA table_info(yard_prices)").use { c -> buildSet { while(c.moveToNext()) add(c.getString(1)) } }; assertTrue(yardCols.containsAll(listOf("latitude","operatingHours","favorite","bundledStarter"))); assertTrue(priceCols.containsAll(listOf("price","unit"))) }
  context.deleteDatabase(name)
 }
}
