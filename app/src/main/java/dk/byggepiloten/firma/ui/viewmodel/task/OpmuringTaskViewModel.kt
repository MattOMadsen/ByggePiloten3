// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/task/OpmuringTaskViewModel.kt
package dk.byggepiloten.firma.ui.viewmodel.task

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.task.Request
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OpmuringTaskViewModel @Inject constructor(
    private val requestRepository: RequestRepository,
    private val application: Application
) : BaseTaskViewModel() {

    private val _wallData = MutableStateFlow(WallData())
    val wallData = _wallData.asStateFlow()

    private val _stepPhotos = MutableStateFlow<Map<String, List<Uri>>>(emptyMap())
    val stepPhotos = _stepPhotos.asStateFlow()

    fun updateWallData(data: WallData) {
        _wallData.value = data
    }

    fun updateStepPhotos(stepId: String, uris: List<Uri>) {
        _stepPhotos.value = _stepPhotos.value.toMutableMap().apply { this[stepId] = uris }
    }

    fun isStepValid(stepNumber: Int): Boolean {
        val d = _wallData.value
        return when (stepNumber) {
            1 -> d.murType != null
            2 -> d.isRepair != null
            3 -> d.bearingWall != null
            4 -> if (d.wallMode == "samlet") (d.wallTotalAreaM2 ?: 0f) > 0f else d.wallMeasurements.isNotEmpty()
            12 -> d.foundationOption != null
            else -> true
        }
    }

    override fun sendTask(onComplete: () -> Unit) {
        viewModelScope.launch {
            setIsSending(true)
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser ?: return@launch
                val d = _wallData.value
                val descText = description.value // Her henter vi teksten fra BaseTaskViewModel
                
                Timber.d("Prøver at sende opgave med beskrivelse: '$descText'")

                val detailsMap = mutableMapOf<String, Any>()
                detailsMap["murType"] = d.murType ?: ""
                detailsMap["isRepair"] = d.isRepair ?: false
                detailsMap["bearingWall"] = d.bearingWall ?: false
                detailsMap["wallTotalAreaM2"] = d.wallTotalAreaM2 ?: 0f
                d.thicknessOption?.let { detailsMap["thicknessOption"] = it }
                d.stoneType?.let { detailsMap["stoneType"] = it }
                d.foundationOption?.let { detailsMap["foundationOption"] = it }

                val netArea = (d.wallTotalAreaM2 ?: 0f) - (d.openingTotalAreaM2 ?: 0f)
                val firestore = Firebase.firestore
                val docRef = firestore.collection("requests").document()
                val requestId = docRef.id

                val initialRequest = Request(
                    id = requestId,
                    userId = currentUser.uid,
                    role = "private",
                    fag = "Murer",
                    category = "Opmuring",
                    areaM2 = netArea,
                    roomType = d.murType ?: "Opmuring",
                    description = descText, // Sikrer at beskrivelsen kommer med her
                    aiPrice = aiPriceEstimate.value?.toFloat() ?: 0f,
                    details = detailsMap
                )

                docRef.set(initialRequest).await()
                Timber.d("Initial request gemt i Firestore")

                val storage = FirebaseStorage.getInstance().reference
                val generalUrls = mutableListOf<String>()
                
                // Upload generelle billeder
                for (uri in imageUris.value) {
                    try {
                        val ref = storage.child("requests/$requestId/${UUID.randomUUID()}.jpg")
                        ref.putFile(uri).await()
                        val url = ref.downloadUrl.await().toString()
                        generalUrls.add(url)
                    } catch (e: Exception) { 
                        Timber.e(e, "Fejl ved upload af billede $uri") 
                    }
                }

                // Upload step-billeder
                val labeledMap = mutableMapOf<String, List<String>>()
                for ((stepId, uris) in _stepPhotos.value) {
                    val urls = mutableListOf<String>()
                    for (uri in uris) {
                        try {
                            val ref = storage.child("requests/$requestId/steps/$stepId/${UUID.randomUUID()}.jpg")
                            ref.putFile(uri).await()
                            urls.add(ref.downloadUrl.await().toString())
                        } catch (e: Exception) { Timber.e(e, "Upload fejl step $stepId") }
                    }
                    if (urls.isNotEmpty()) {
                        val prettyLabel = when(stepId) {
                            "damage" -> "Billeder af skader"
                            "access" -> "Billeder af adgangsforhold"
                            "openings" -> "Billeder af åbninger"
                            else -> stepId
                        }
                        labeledMap[prettyLabel] = urls
                    }
                }

                // Opdater dokumentet med de endelige URL'er
                if (generalUrls.isNotEmpty() || labeledMap.isNotEmpty()) {
                    docRef.update(mapOf(
                        "images" to generalUrls,
                        "labeledPhotos" to labeledMap
                    )).await()
                    Timber.d("Firestore opdateret med URL'er")
                }

                onComplete()
            } catch (e: Exception) {
                Timber.e(e, "Kritisk fejl i sendTask")
            } finally {
                setIsSending(false)
            }
        }
    }
}
