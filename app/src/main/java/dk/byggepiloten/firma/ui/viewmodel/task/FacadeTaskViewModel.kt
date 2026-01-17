// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/task/FacadeTaskViewModel.kt
// FULD OPDATERET – TILFØJET REEL BILLEDE-UPLOAD (general) MED SERVER-ID FLOW
// + images sættes som URLs (ikke lokale Uri strings)
// + Temp request → create → find nyeste → upload → update
// + ca. 230 linjer

package dk.byggepiloten.firma.ui.viewmodel.task

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.task.FacadeData
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
class FacadeTaskViewModel @Inject constructor(
    private val requestRepository: RequestRepository
) : BaseTaskViewModel() {

    private val _facadeData = MutableStateFlow(FacadeData())
    val facadeData = _facadeData.asStateFlow()

    fun updateFacadeData(newData: FacadeData) {
        _facadeData.value = newData
    }

    override fun sendTask(onComplete: () -> Unit) {
        viewModelScope.launch {
            setIsSending(true)
            try {
                val d = _facadeData.value
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("Ingen bruger")

                val areaM2 = d.area ?: 0f

                val detailsMap = mapOf<String, Any>(
                    "areaM2" to areaM2,
                    "vaegtype" to (d.vaegtype ?: ""),
                    "andenVaegtype" to (d.andenVaegtype ?: ""),
                    "hojde" to (d.hojde ?: 0f),
                    "stilladsNoedvendigt" to (d.stilladsNoedvendigt ?: ""),
                    "stilladsAdgang" to (d.stilladsAdgang ?: ""),
                    "stilladsTrapper" to (d.stilladsTrapper ?: ""),
                    "armeringsnet" to (d.armeringsnet ?: ""),
                    "isolering" to (d.isolering ?: ""),
                    "isoleringType" to (d.isoleringType ?: ""),
                    "underlagRevner" to (d.underlagRevner ?: ""),
                    "underlagFugt" to (d.underlagFugt ?: ""),
                    "underlagGammelPuds" to (d.underlagGammelPuds ?: ""),
                    "vejretidspunkt" to (d.vejretidspunkt ?: ""),
                    "haeftemoertelType" to (d.haeftemoertelType ?: ""),
                    "andenHaeftemoertel" to (d.andenHaeftemoertel ?: ""),
                    "durapudsFarve" to (d.durapudsFarve ?: ""),
                    "skalcemFarve" to (d.skalcemFarve ?: "")
                )

                val tempRequest = Request(
                    userId = userId,
                    role = "private",
                    fag = "Murer",
                    category = "facade_pudsning",
                    areaM2 = areaM2,
                    roomType = "Facadepudsning",
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
                Timber.d("Facade opgave sendt med billeder")
                onComplete()
            } catch (e: Exception) {
                Timber.e(e, "Send task fejl (facade)")
            } finally {
                setIsSending(false)
            }
        }
    }
}