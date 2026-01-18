// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/task/FacadeTaskViewModel.kt
// FULD OPDATERET – SAVE-LOGIK SOM OPMURING (alle felter gemmes i detailsMap)
// + StepPhotos (Map<String, List<Uri>>) – upload + labels (f.eks. "Billeder af underlag")
// + Beregn areaM2 = area (ingen åbninger i facade)
// + Arver fra BaseTaskViewModel
// RETTET: Tilføjet aiEstimateGenerator til constructor.

package dk.byggepiloten.firma.ui.viewmodel.task

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.task.FacadeData
import dk.byggepiloten.firma.data.model.task.Request
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FacadeTaskViewModel @Inject constructor(
    private val application: Application,
    aiEstimateGenerator: AiEstimateGenerator
) : BaseTaskViewModel(aiEstimateGenerator) {

    private val _facadeData = MutableStateFlow(FacadeData())
    val facadeData = _facadeData.asStateFlow()

    private val _stepPhotos = MutableStateFlow<Map<String, List<Uri>>>(emptyMap())
    val stepPhotos = _stepPhotos.asStateFlow()

    fun updateFacadeData(data: FacadeData) {
        _facadeData.value = data
    }

    fun updateStepPhotos(stepId: String, uris: List<Uri>) {
        _stepPhotos.value = _stepPhotos.value.toMutableMap().apply { this[stepId] = uris }
    }

    override fun sendTask(onComplete: () -> Unit) {
        viewModelScope.launch {
            setIsSending(true)
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser ?: return@launch
                val d = _facadeData.value
                val descText = description.value

                val firestore = Firebase.firestore
                val docRef = firestore.collection("requests").document()
                val requestId = docRef.id

                // 1. Upload billeder
                val storage = FirebaseStorage.getInstance("gs://byg-piloten.firebasestorage.app").reference
                val generalUrls = mutableListOf<String>()
                for (uri in imageUris.value) {
                    try {
                        val ref = storage.child("requests/$requestId/general/${UUID.randomUUID()}.jpg")
                        ref.putFile(uri).await()
                        generalUrls.add(ref.downloadUrl.await().toString())
                    } catch (e: Exception) { Timber.e(e, "General upload fejl") }
                }

                val labeledUrlsMap = mutableMapOf<String, List<String>>()
                for ((stepId, uris) in _stepPhotos.value) {
                    val urls = mutableListOf<String>()
                    for (uri in uris) {
                        try {
                            val ref = storage.child("requests/$requestId/steps/$stepId/${UUID.randomUUID()}.jpg")
                            ref.putFile(uri).await()
                            urls.add(ref.downloadUrl.await().toString())
                        } catch (e: Exception) { Timber.e(e, "Step upload fejl") }
                    }
                    if (urls.isNotEmpty()) {
                        val label = when(stepId) {
                            "underlag" -> "Billeder af underlag"
                            "stillads" -> "Billeder af adgang/stillads"
                            "haeftemoertel" -> "Billeder af eksisterende puds"
                            else -> "Billeder fra $stepId"
                        }
                        labeledUrlsMap[label] = urls
                    }
                }

                // 2. Byg komplet detailsMap med ALLE felter
                val detailsMap = mutableMapOf<String, Any>()

                d.area?.let { detailsMap["area"] = it }
                d.vaegtype?.let { detailsMap["vaegtype"] = it }
                d.andenVaegtype?.let { detailsMap["andenVaegtype"] = it }
                d.hojde?.let { detailsMap["hojde"] = it }
                d.stilladsNoedvendigt?.let { detailsMap["stilladsNoedvendigt"] = it }
                d.stilladsAdgang?.let { detailsMap["stilladsAdgang"] = it }
                d.stilladsTrapper?.let { detailsMap["stilladsTrapper"] = it }
                d.armeringsnet?.let { detailsMap["armeringsnet"] = it }
                d.isolering?.let { detailsMap["isolering"] = it }
                d.isoleringType?.let { detailsMap["isoleringType"] = it }
                d.underlagRevner?.let { detailsMap["underlagRevner"] = it }
                d.underlagFugt?.let { detailsMap["underlagFugt"] = it }
                d.underlagGammelPuds?.let { detailsMap["underlagGammelPuds"] = it }
                d.vejretidspunkt?.let { detailsMap["vejretidspunkt"] = it }
                d.haeftemoertelType?.let { detailsMap["haeftemoertelType"] = it }
                d.andenHaeftemoertel?.let { detailsMap["andenHaeftemoertel"] = it }
                d.durapudsFarve?.let { detailsMap["durapudsFarve"] = it }
                d.skalcemFarve?.let { detailsMap["skalcemFarve"] = it }

                // 3. Opret Request
                val request = Request(
                    id = requestId,
                    userId = currentUser.uid,
                    category = "facade_pudsning",
                    areaM2 = d.area ?: 0f,
                    roomType = "Facade",
                    description = descText.ifBlank { null },
                    aiPrice = aiPriceEstimate.value?.toFloat() ?: 0f,
                    images = generalUrls,
                    labeledPhotos = labeledUrlsMap,
                    details = detailsMap
                )

                docRef.set(request).await()
                Timber.d("Facade-opgave gemt med fuld detailsMap")

                onComplete()
            } catch (e: Exception) {
                Timber.e(e, "Fejl ved afsendelse")
            } finally {
                setIsSending(false)
            }
        }
    }
}