// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/task/FacadeTaskViewModel.kt
// OPDATERET – tilføjet fuld override sendTask
// • Beregner areaM2 direkte fra data.area (fallback 0f)
// • Fuld detailsMap med ALLE felter fra FacadeData (inkl. conditional)
// • Bruger lokal private val requestRepository
// • Kategori "facade_pudsning", roomType "Facadepudsning"
// • Linjer: 178 (bekræftet)

package dk.byggepiloten.firma.ui.viewmodel.task

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.task.FacadeData
import dk.byggepiloten.firma.data.model.task.Request
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth

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
                val category = currentCategory.value.ifBlank { "facade_pudsning" }
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

                val request = Request(
                    userId = userId,
                    role = "private",
                    fag = "Murer",
                    category = "facade_pudsning",
                    areaM2 = areaM2,
                    roomType = "Facadepudsning",
                    requiresMembrane = false,
                    aiPrice = (aiPriceEstimate.value ?: 0L).toFloat(),
                    images = imageUris.value.map { it.toString() },
                    description = description.value,
                    status = "new"
                ).apply {
                    details = detailsMap
                }

                requestRepository.createRequest(request)
                Timber.d("Opgave sendt (facade_pudsning)")
                onComplete()
            } catch (e: Exception) {
                Timber.e(e, "Send task fejl (facade_pudsning): ${e.message}")
            } finally {
                setIsSending(false)
            }
        }
    }
}