package com.example.ui.calculators

import android.app.Application
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.local.ScrapProDatabase
import com.example.data.repository.TowProfile
import com.example.data.repository.TowProfileRepository
import com.example.domain.engine.*
import com.example.domain.model.TowSafetyInput
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private data class InputField(val key:String,val label:String,val group:String)
private val fields=listOf(
 InputField("vehicleGvwr","Vehicle GVWR","Vehicle ratings"),InputField("vehicleCurbWeight","Vehicle curb weight","Vehicle ratings"),InputField("ratedPayload","Rated payload","Vehicle ratings"),InputField("frontGawr","Front GAWR","Vehicle ratings"),InputField("rearGawr","Rear GAWR","Vehicle ratings"),InputField("receiverRating","Receiver/hitch rating","Vehicle ratings"),InputField("maxTongueWeight","Maximum tongue weight","Vehicle ratings"),
 InputField("measuredFrontAxle","Measured front axle","Scale measurements"),InputField("measuredRearAxle","Measured rear axle","Scale measurements"),InputField("measuredTrailerAxle","Measured trailer axle (optional)","Scale measurements"),InputField("measuredTongueWeight","Measured tongue weight","Scale measurements"),
 InputField("trailerGvwr","Trailer GVWR","Trailer ratings"),InputField("trailerEmptyWeight","Trailer empty weight","Trailer ratings"),InputField("trailerAxleRating","Trailer axle rating","Trailer ratings"),InputField("trailerCargoWeight","Trailer cargo","Cargo and placement"),InputField("passengerCabCargo","Passenger/cab cargo","Cargo and placement"),InputField("truckBedCargo","Truck-bed cargo","Cargo and placement"),InputField("truckCargoPosition","Truck cargo position (-1 to 1)","Cargo and placement"),InputField("trailerCargoPosition","Trailer cargo position (-1 to 1)","Cargo and placement"),InputField("safetyMarginPercent","Optional safety margin %","Cargo and placement")
)

data class PayloadSafetyUiState(val values:Map<String,String> = fields.associate { it.key to "" },val result:PayloadSafetyResult=PayloadSafetyEngine().evaluate(TowSafetyInput()),val profiles:List<TowProfile> = emptyList(),val message:String?=null)

class PayloadSafetyViewModel(application:Application):AndroidViewModel(application){
 private val repository=TowProfileRepository(ScrapProDatabase.getDatabase(application).towProfileDao())
 private val engine=PayloadSafetyEngine(); private val _state=MutableStateFlow(PayloadSafetyUiState()); val uiState:StateFlow<PayloadSafetyUiState> = _state.asStateFlow(); private var saveJob:Job?=null
 init { viewModelScope.launch { repository.observeCurrent().first()?.let(::loadInput) }; viewModelScope.launch { repository.observeNamed().collect { _state.update { s->s.copy(profiles=it) } } } }
 fun update(key:String,value:String){ val values=_state.value.values+(key to value); val input=parse(values); _state.value=_state.value.copy(values=values,result=engine.evaluate(input),message=null); saveJob?.cancel(); saveJob=viewModelScope.launch { delay(300); repository.saveCurrent(input) } }
 fun saveNamed(name:String)=viewModelScope.launch { runCatching { repository.saveNamed(name,parse(_state.value.values)) }.fold({ _state.update { it.copy(message="Profile saved") } },{e->_state.update { it.copy(message=e.message?:"Profile save failed") }}) }
 fun load(profile:TowProfile){ loadInput(profile.input); viewModelScope.launch { repository.saveCurrent(profile.input) } }
 fun delete(profile:TowProfile)=viewModelScope.launch { repository.delete(profile.id) }
 fun clearMessage(){ _state.update { it.copy(message=null) } }
 private fun loadInput(input:TowSafetyInput){ val values=mapOf("vehicleGvwr" to input.vehicleGvwr,"vehicleCurbWeight" to input.vehicleCurbWeight,"ratedPayload" to input.ratedPayload,"frontGawr" to input.frontGawr,"rearGawr" to input.rearGawr,"measuredFrontAxle" to input.measuredFrontAxle,"measuredRearAxle" to input.measuredRearAxle,"receiverRating" to input.receiverRating,"maxTongueWeight" to input.maxTongueWeight,"trailerGvwr" to input.trailerGvwr,"trailerEmptyWeight" to input.trailerEmptyWeight,"trailerAxleRating" to input.trailerAxleRating,"measuredTrailerAxle" to input.measuredTrailerAxle,"trailerCargoWeight" to input.trailerCargoWeight,"measuredTongueWeight" to input.measuredTongueWeight,"passengerCabCargo" to input.passengerCabCargo,"truckBedCargo" to input.truckBedCargo,"truckCargoPosition" to input.truckCargoPosition,"trailerCargoPosition" to input.trailerCargoPosition,"safetyMarginPercent" to input.safetyMarginPercent).mapValues { if(it.value==0.0) "" else it.value.toString() }; _state.value=_state.value.copy(values=values,result=engine.evaluate(input)) }
 private fun parse(v:Map<String,String>)=TowSafetyInput(v.d("vehicleGvwr"),v.d("vehicleCurbWeight"),v.d("ratedPayload"),v.d("frontGawr"),v.d("rearGawr"),v.d("measuredFrontAxle"),v.d("measuredRearAxle"),v.d("receiverRating"),v.d("maxTongueWeight"),v.d("trailerGvwr"),v.d("trailerEmptyWeight"),v.d("trailerAxleRating"),v.d("measuredTrailerAxle"),v.d("trailerCargoWeight"),v.d("measuredTongueWeight"),v.d("passengerCabCargo"),v.d("truckBedCargo"),v.d("truckCargoPosition"),v.d("trailerCargoPosition"),v.d("safetyMarginPercent"))
 private fun Map<String,String>.d(k:String)=get(k)?.toDoubleOrNull()?:0.0
}

@Composable fun CapacityMeter(title:String,status:LimitStatus){ val color=status.classification.color; val width=(status.percentage/100).toFloat().coerceIn(0f,1f); Column(Modifier.fillMaxWidth().padding(vertical=5.dp)){ Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text(title);Text("${"%.0f".format(status.current)} / ${"%.0f".format(status.limit)} lb (${"%.1f".format(status.percentage)}%)",color=color)}; LinearProgressIndicator(progress={width},modifier=Modifier.fillMaxWidth(),color=color) } }

@Composable fun LoadDistributionVisualizer(input:TowSafetyInput,result:PayloadSafetyResult){ Canvas(Modifier.fillMaxWidth().height(150.dp)){ val y=size.height*.62f; drawLine(Color.Gray,Offset(10f,y),Offset(size.width-10f,y),6f); drawRect(Color(0xFF374151),Offset(size.width*.05f,y-45),Size(size.width*.34f,40f)); drawCircle(Color.LightGray,14f,Offset(size.width*.15f,y+8)); drawCircle(Color.LightGray,14f,Offset(size.width*.34f,y+8)); drawRect(result.hitchStatus.classification.color,Offset(size.width*.4f,y-5),Size(size.width*.08f,8f)); drawRect(Color(0xFF374151),Offset(size.width*.49f,y-35),Size(size.width*.45f,30f)); drawCircle(result.trailerAxleStatus.classification.color,16f,Offset(size.width*.76f,y+10)); val truckX=size.width*(.22f+input.truckCargoPosition.toFloat()*.10f); val trailerX=size.width*(.72f+input.trailerCargoPosition.toFloat()*.16f); drawCircle(Color(0xFFF97316),12f,Offset(truckX,y-55)); drawCircle(Color(0xFFFBBF24),12f,Offset(trailerX,y-48)) } }

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun PayloadSafetyScreen(viewModel:PayloadSafetyViewModel=androidx.lifecycle.viewmodel.compose.viewModel()){
 val state by viewModel.uiState.collectAsState(); var profileName by remember { mutableStateOf("") }; val result=state.result
 Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())){
  com.example.ui.core.ScreenHeaderBanner(R.drawable.load_bal,"Load Balancer")
  Column(Modifier.padding(16.dp)){
   Text("Axle-Level Tow Safety",style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)
   Surface(color=Color(0x33FBBF24),shape=RoundedCornerShape(8.dp),modifier=Modifier.fillMaxWidth().padding(vertical=10.dp)){ Text("Planning aid only—not legal or manufacturer approval. Confirm door-jamb labels, owner manuals, receiver and hitch ratings, trailer labels, tire ratings, and certified scale measurements before towing.",Modifier.padding(12.dp),color=Color(0xFFFBBF24)) }
   LoadDistributionVisualizer(parseForUi(state.values),result)
   ResultCard(result)
   fields.groupBy { it.group }.forEach { (group,list)-> Text(group,style=MaterialTheme.typography.titleMedium,modifier=Modifier.padding(top=14.dp)); list.chunked(2).forEach { row->Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){row.forEach { f-> val raw=state.values[f.key].orEmpty();val parseError=raw.isNotBlank()&&raw.toDoubleOrNull()==null;OutlinedTextField(raw,{viewModel.update(f.key,it)},label={Text(f.label)},isError=parseError||result.fieldErrors.containsKey(f.key),supportingText={val error=if(parseError)"Enter a valid number" else result.fieldErrors[f.key];error?.let{Text(it)}},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),modifier=Modifier.weight(1f))};if(row.size==1)Spacer(Modifier.weight(1f)) } } }
   Text("Named profiles",style=MaterialTheme.typography.titleMedium,modifier=Modifier.padding(top=16.dp)); Row{OutlinedTextField(profileName,{profileName=it},label={Text("Profile name")},modifier=Modifier.weight(1f));Button(onClick={viewModel.saveNamed(profileName);profileName=""},enabled=profileName.isNotBlank(),modifier=Modifier.height(56.dp)){Text("Save")}}
   state.profiles.forEach { p->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){TextButton(onClick={viewModel.load(p)}){Text(p.name)};TextButton(onClick={viewModel.delete(p)}){Text("Delete")}} }
  }
 }
 state.message?.let { AlertDialog(onDismissRequest=viewModel::clearMessage,text={Text(it)},confirmButton={TextButton(onClick=viewModel::clearMessage){Text("OK")}}) }
}

@Composable private fun ResultCard(r:PayloadSafetyResult){ OutlinedCard(Modifier.fillMaxWidth()){Column(Modifier.padding(14.dp)){Text("Calculated limits",fontWeight=FontWeight.Bold);Text("Available payload: ${"%.0f".format(r.availableVehiclePayload)} lb · Loaded vehicle: ${"%.0f".format(r.loadedVehicleWeight)} lb");Text("Estimated axles: front ${"%.0f".format(r.estimatedFrontAxleLoad)} lb · rear ${"%.0f".format(r.estimatedRearAxleLoad)} lb");Text("Trailer payload capacity: ${"%.0f".format(r.trailerPayloadCapacity)} lb · loaded ${"%.0f".format(r.loadedTrailerWeight)} lb");Text("Tongue: ${"%.1f".format(r.tongueWeightPercentage)}% · exact range ${"%.0f".format(r.minimumSafeTongueWeight)}–${"%.0f".format(r.maximumSafeTongueWeight)} lb",color=r.tongueStatus.color);CapacityMeter("Hitch",r.hitchStatus);CapacityMeter("Vehicle GVWR",r.vehicleStatus);CapacityMeter("Front GAWR",r.frontAxleStatus);CapacityMeter("Rear GAWR",r.rearAxleStatus);CapacityMeter("Trailer GVWR",r.trailerStatus);CapacityMeter("Trailer axle",r.trailerAxleStatus);Text("Corrective recommendations",fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=8.dp));r.recommendations.forEach{Text("• $it")} }}}
private val SafetyClass.color get()=when(this){SafetyClass.GREEN->Color(0xFF34D399);SafetyClass.YELLOW->Color(0xFFFBBF24);SafetyClass.RED->Color(0xFFEF4444)}
private fun parseForUi(v:Map<String,String>)=TowSafetyInput(truckCargoPosition=v["truckCargoPosition"]?.toDoubleOrNull()?:0.0,trailerCargoPosition=v["trailerCargoPosition"]?.toDoubleOrNull()?:0.0)
