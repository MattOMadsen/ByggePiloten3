// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/task/PudsTaskViewModel.kt
// OPDATERET – wallMeasurements i stedet for vaegMaalinger (konsistens med opmuring)
// - Trådsikker updatePudsData med .update { }
// - Konsistent med PudsAreaStep og Opmuring-struktur

package dk.byggepiloten.firma.ui.viewmodel.task

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.task.PudsData
import dk.byggepiloten.firma.data.model.task.WallMeasurement
import dk.byggepiloten.firma.data.repository.AuthRepository
import dk.byggepiloten.firma.ui.screen.new_task.categories.puds.PudsValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class PudsTaskViewModel @Inject constructor(
    aiEstimateGenerator: AiEstimateGenerator,
    private val authRepository: AuthRepository
) : BaseTaskViewModel(aiEstimateGenerator) {

    private val _pudsData = MutableStateFlow(PudsData())
    val pudsData: StateFlow<PudsData> = _pudsData.asStateFlow()

    private val _stepPhotos = MutableStateFlow<Map<String, List<Uri>>>(emptyMap())
    val stepPhotos: StateFlow<Map<String, List<Uri>>> = _stepPhotos.asStateFlow()

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun updatePudsData(newData: PudsData) {
        _pudsData.update { newData }
    }

    fun updateStepPhotos(stepKey: String, uris: List<Uri>) {
        _stepPhotos.update { current ->
            current.toMutableMap().apply { this[stepKey] = uris }
        }
    }

    fun validateBeforeSend(): List<Int> {
        val data = _pudsData.value
        val photos = _stepPhotos.value
        return (1..10).filter { step ->
            val skipped = data.indeUde == "Inde" && step in listOf(4, 5, 7, 8)
            !skipped && !PudsValidator.isStepValid(data, photos, step)
        }.sorted()
    }

    override fun sendTask(onSuccess: () -> Unit) {
        viewModelScope.launch {
            setIsSending(true)
            try {
                setError(null)

                val data = _pudsData.value
                val allUris = imageUris.value + stepPhotos.value.values.flatten()

                val imageUrls = mutableListOf<String>()
                for (uri in allUris) {
                    val ref = storage.reference.child("tasks/puds/${System.currentTimeMillis()}_${uri.lastPathSegment}")
                    ref.putFile(uri).await()
                    val url = ref.downloadUrl.await().toString()
                    imageUrls.add(url)
                }

                val taskMap = data.toMap().toMutableMap()
                taskMap["category"] = "pudsning"
                taskMap["imageUrls"] = imageUrls
                taskMap["createdAt"] = FieldValue.serverTimestamp()
                taskMap["userId"] = authRepository.getCurrentUser()?.uid ?: "unknown"
                taskMap["status"] = "ny"

                firestore.collection("tasks").add(taskMap).await()
                onSuccess()
            } catch (e: Exception) {
                setError("Fejl ved afsendelse: ${e.localizedMessage ?: "Ukendt fejl"}")
            } finally {
                setIsSending(false)
            }
        }
    }

    override fun generateAiEstimate(areaM2: Float, extraDetails: String?) {
        val currentData = _pudsData.value
        val pudsDetails = extraDetails ?: "Pudsning ${currentData.indeUde?.lowercase() ?: ""}"
        super.generateAiEstimate(areaM2, pudsDetails)
    }
}
