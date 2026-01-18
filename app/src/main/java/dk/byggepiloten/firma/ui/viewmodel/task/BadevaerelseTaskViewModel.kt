// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/task/BadevaerelseTaskViewModel.kt
// FULD OPDATERET – TILFØJET REEL BILLEDE-UPLOAD (general) MED SERVER-ID FLOW
// + images sættes som URLs
// RETTET: Tilføjet aiEstimateGenerator til constructor.

package dk.byggepiloten.firma.ui.viewmodel.task

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.task.BadevaerelseData
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
class BadevaerelseTaskViewModel @Inject constructor(
    private val requestRepository: RequestRepository,
    aiEstimateGenerator: AiEstimateGenerator
) : BaseTaskViewModel(aiEstimateGenerator) {

    private val _badevaerelseData = MutableStateFlow(BadevaerelseData())
    val badevaerelseData = _badevaerelseData.asStateFlow()

    fun updateBadevaerelseData(data: BadevaerelseData) {
        _badevaerelseData.value = data
    }

    override fun sendTask(onComplete: () -> Unit) {
        viewModelScope.launch {
            setIsSending(true)
            try {
                val d = _badevaerelseData.value
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("Ingen bruger")

                val floorArea = (d.floorLength ?: 0f) * (d.floorWidth ?: 0f)
                val netArea = d.wallManualArea ?: floorArea

                val detailsMap = mapOf<String, Any>(
                    "renovationType" to (d.renovationType ?: ""),
                    "floorLength" to (d.floorLength ?: 0f),
                    "floorWidth" to (d.floorWidth ?: 0f),
                    "wallHeight" to (d.wallHeight ?: 0f),
                    "netArea" to netArea
                )

                val tempRequest = Request(
                    userId = userId,
                    role = "private",
                    fag = "Murer",
                    category = "badeværelse",
                    areaM2 = netArea,
                    roomType = "Badeværelse",
                    requiresMembrane = d.hasMembrane ?: true,
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
                Timber.d("Badeværelse opgave sendt med billeder")
                onComplete()
            } catch (e: Exception) {
                Timber.e(e, "Send task fejl (badeværelse)")
            } finally {
                setIsSending(false)
            }
        }
    }
}