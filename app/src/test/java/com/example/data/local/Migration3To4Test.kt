package com.example.data.local

import android.content.Context
import androidx.sqlite.db.*
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Migration3To4Test { @Test fun createsTowProfiles(){val c=ApplicationProvider.getApplicationContext<Context>();val n="m34-${System.nanoTime()}.db";val cfg=SupportSQLiteOpenHelper.Configuration.builder(c).name(n).callback(object:SupportSQLiteOpenHelper.Callback(3){override fun onCreate(db:SupportSQLiteDatabase)=Unit;override fun onUpgrade(db:SupportSQLiteDatabase,o:Int,n:Int)=Unit}).build();FrameworkSQLiteOpenHelperFactory().create(cfg).use{h->val db=h.writableDatabase;ScrapProDatabase.MIGRATION_3_4.migrate(db);val names=db.query("PRAGMA table_info(tow_profiles)").use{q->buildSet{while(q.moveToNext())add(q.getString(1))}};assertTrue(names.containsAll(listOf("id","name","encodedInput","updatedAt")))};c.deleteDatabase(n)} }
