// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/task/FliserTaskViewModel.kt
// FULD OPDATERET – TILFØJET REEL BILLEDE-UPLOAD (general) MED SERVER-ID FLOW
// + images sættes som URLs
// RETTET: Tilføjet aiEstimateGenerator til constructor.

package dk.byggepiloten.firma.ui.viewmodel.task

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.task.FliserData
import dk.byggepiloten.firma.data.model.task.Request
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FliserTaskViewModel @Inject constructor(
    private val requestRepository: RequestRepository,
    aiEstimateGenerator: AiEstimateGenerator
) : BaseTaskViewModel(aiEstimateGenerator) {

    private val _fliserData = MutableStateFlow(FliserData())
    val fliserData = _fliserData.asStateFlow()

    fun updateFliserData(data: FliserData) {
        _fliserData.value = data
    }

    override fun sendTask(onComplete: () -> Unit) {
        viewModelScope.launch {
            setIsSending(true)
            try {
                val d = _fliserData.value
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("Ingen bruger")

                val floorArea = (d.floorLength ?: 0f) * (d.floorWidth ?: 0f)
                val wallArea = (d.manualWallPerimeter ?: 0f) * (d.wallHeight ?: 0f)
                val totalArea = floorArea + wallArea
                val netArea = (totalArea - (d.deductionArea ?: 0f)).coerceAtLeast(0f)

                val detailsMap = mapOf<String, Any>(
                    "workType" to (d.workType ?: ""),
                    "floorLength" to (d.floorLength ?: 0f),
                    "floorWidth" to (d.floorWidth ?: 0f),
                    "netArea" to netArea
                )

                val tempRequest = Request(
                    userId = userId,
                    role = "private",
                    fag = "Murer",
                    category = "flise_klinke",
                    areaM2 = netArea,
                    roomType = "Flise- og klinkearbejde",
                    requiresMembrane = false,
                    aiPrice = (aiPriceEstimate.value ?: 0L).toFloat(),
                    images = emptyList(),
                    description = description.value,
                    status = "new"
                ).apply {
                    details = detailsMap
                }

                requestRepository.createRequest(tempRequest)

                val userRequests = requestRepository.getUserRequests() ?: emptyList()
                val newRequest = userRequests.maxByOrNull { it.sentAt }
                    ?: throw Exception("Kunne ikke finde ny opgave")

                val requestId = newRequest.id
                val storage = FirebaseStorage.getInstance()

                val generalUrls = imageUris.value.mapNotNull { uri ->
                    try {
                        val ref = storage.reference.child("requests/$requestId/general/${UUID.randomUUID()}")
                        ref.putFile(uri).await()
                        ref.downloadUrl.await().toString()
                    } catch (e: Exception) {
                        Timber.e(e, "General upload fejl")
                        null
                    }
                }

                val updatedRequest = newRequest.copy(
                    images = generalUrls
                )

                requestRepository.updateRequest(updatedRequest)
                Timber.d("Fliser opgave sendt med billeder")
                onComplete()
            } catch (e: Exception) {
                Timber.e(e, "Send task fejl (fliser)")
            } finally {
                setIsSending(false)
            }
        }
    }
}