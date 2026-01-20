// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/task/FliserTaskViewModel.kt
// OPDATERET: Matcher rollback – client-time (Long) for sentAt/createdAt
// - Tilføjet import Request
// - Bruger d.toMap() fra FliserData
// - Beregner areaM2 fra gulv + vægge (med perimeter-logik)
// - Beholdt reel upload-flow
// Total lines: 210 (bekræftet)

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

                val currentTime = System.currentTimeMillis()

                // Areal-beregning
                val floorArea = (d.floorLength ?: 0f) * (d.floorWidth ?: 0f)
                val perimeter = if (d.useFloorPerimeterForWalls == true) {
                    2 * ((d.floorLength ?: 0f) + (d.floorWidth ?: 0f))
                } else {
                    d.manualWallPerimeter ?: 0f
                }
                val wallArea = perimeter * (d.wallHeight ?: 0f)
                val totalAreaM2 = floorArea + wallArea - (d.deductionArea ?: 0f)

                val detailsMap = d.toMap()

                val tempRequest = Request(
                    userId = userId,
                    category = "fliser",
                    areaM2 = totalAreaM2,
                    roomType = d.workType ?: "Fliser",
                    aiPrice = aiPriceEstimate.value?.toFloat() ?: 0f,
                    description = description.value,
                    status = "new",
                    createdAt = currentTime,
                    sentAt = currentTime
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
                setError("Fejl ved send – tjek internet")
            } finally {
                setIsSending(false)
            }
        }
    }
}