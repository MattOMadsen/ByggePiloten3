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

    override fun sendTask(onComplete: () -> Unit) {
        viewModelScope.launch {
            setIsSending(true)
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser ?: return@launch
                val d = _wallData.value
                val descText = description.value
                
                val firestore = Firebase.firestore
                val docRef = firestore.collection("requests").document()
                val requestId = docRef.id

                // 1. Upload billeder FØRST, så vi har alle URL'er klar
                val storage = FirebaseStorage.getInstance("gs://byg-piloten.firebasestorage.app").reference
                val generalUrls = mutableListOf<String>()
                for (uri in imageUris.value) {
                    try {
                        val ref = storage.child("requests/$requestId/general/${UUID.randomUUID()}.jpg")
                        ref.putFile(uri).await()
                        generalUrls.add(ref.downloadUrl.await().toString())
                    } catch (e: Exception) { Timber.e(e, "Upload fejl") }
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
                            "damage" -> "Billeder af skader"
                            "access" -> "Billeder af adgangsforhold"
                            "openings" -> "Billeder af åbninger"
                            else -> stepId
                        }
                        labeledUrlsMap[label] = urls
                    }
                }

                // 2. Gem det HELE i Firestore i ét hug
                val detailsMap = mutableMapOf<String, Any>()
                detailsMap["murType"] = d.murType ?: ""
                detailsMap["isRepair"] = d.isRepair ?: false
                detailsMap["bearingWall"] = d.bearingWall ?: false
                detailsMap["wallTotalAreaM2"] = d.wallTotalAreaM2 ?: 0f

                val request = Request(
                    id = requestId,
                    userId = currentUser.uid,
                    category = "opmuring",
                    areaM2 = (d.wallTotalAreaM2 ?: 0f) - (d.openingTotalAreaM2 ?: 0f),
                    roomType = d.murType ?: "Opmuring",
                    description = descText,
                    aiPrice = aiPriceEstimate.value?.toFloat() ?: 0f,
                    images = generalUrls,
                    labeledPhotos = labeledUrlsMap,
                    details = detailsMap
                )

                docRef.set(request).await()
                Timber.d("Opgave og billeder gemt korrekt i Firestore")

                onComplete()
            } catch (e: Exception) {
                Timber.e(e, "Kritisk fejl ved afsendelse")
            } finally {
                setIsSending(false)
            }
        }
    }
}
