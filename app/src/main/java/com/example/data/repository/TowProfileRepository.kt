package com.example.data.repository

import com.example.data.local.dao.TowProfileDao
import com.example.data.local.entity.TowProfileEntity
import com.example.domain.model.TowSafetyInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

data class TowProfile(val id:Long=0,val name:String,val input:TowSafetyInput)

class TowProfileRepository(private val dao:TowProfileDao, private val clock:()->Long=System::currentTimeMillis) {
 fun observeCurrent():Flow<TowSafetyInput?> = dao.observeAll().map { rows -> rows.firstOrNull { it.name==CURRENT }?.decode() }
 fun observeNamed():Flow<List<TowProfile>> = dao.observeAll().map { rows -> rows.filter { it.name!=CURRENT }.map { TowProfile(it.id,it.name,it.decode()) } }
 suspend fun saveCurrent(input:TowSafetyInput){ val existing=dao.observeAll().firstValue().firstOrNull { it.name==CURRENT }; dao.upsert(TowProfileEntity(existing?.id?:0,CURRENT,encode(input),clock())) }
 suspend fun saveNamed(name:String,input:TowSafetyInput){ require(name.isNotBlank()); dao.upsert(TowProfileEntity(name=name.trim(),encodedInput=encode(input),updatedAt=clock())) }
 suspend fun delete(id:Long)=dao.delete(id)
 private fun TowProfileEntity.decode()=decode(encodedInput)
 companion object {
  const val CURRENT="__CURRENT__"
  fun encode(i:TowSafetyInput)=listOf(i.vehicleGvwr,i.vehicleCurbWeight,i.ratedPayload,i.frontGawr,i.rearGawr,i.measuredFrontAxle,i.measuredRearAxle,i.receiverRating,i.maxTongueWeight,i.trailerGvwr,i.trailerEmptyWeight,i.trailerAxleRating,i.measuredTrailerAxle,i.trailerCargoWeight,i.measuredTongueWeight,i.passengerCabCargo,i.truckBedCargo,i.truckCargoPosition,i.trailerCargoPosition,i.safetyMarginPercent).joinToString("|")
  fun decode(value:String):TowSafetyInput { val v=value.split('|').map { it.toDoubleOrNull()?:0.0 }.plus(List(20){0.0}); return TowSafetyInput(v[0],v[1],v[2],v[3],v[4],v[5],v[6],v[7],v[8],v[9],v[10],v[11],v[12],v[13],v[14],v[15],v[16],v[17],v[18],v[19]) }
 }
}
private suspend fun <T> Flow<T>.firstValue():T = first()
