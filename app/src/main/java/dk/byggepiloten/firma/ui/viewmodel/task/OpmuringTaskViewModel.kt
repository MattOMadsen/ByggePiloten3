package dk.byggepiloten.firma.ui.viewmodel.task

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.task.Request
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth

@HiltViewModel
class OpmuringTaskViewModel @Inject constructor(
    requestRepository: RequestRepository
) : BaseTaskViewModel(requestRepository) {

    private val _wallData = MutableStateFlow(WallData())
    val wallData = _wallData.asStateFlow()

    fun updateWallData(data: WallData) {
        _wallData.value = data
    }

    override fun sendTask(onComplete: () -> Unit) {
        viewModelScope.launch {
            setIsSending(true)
            try {
                val category = currentCategory.value ?: "opmuring"
                val d = _wallData.value
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("Ingen bruger")

                // Beregn netto areal
                val totalArea = d.wallMeasurements.sumOf { (it.length ?: 0f).toDouble() * (it.height ?: 0f).toDouble() }.toFloat()
                val openingsArea = d.openingMeasurements.sumOf { (it.widthCm ?: 0f).toDouble() * (it.heightCm ?: 0f).toDouble() / 10000.0 }.toFloat()
                
                val netArea = if (d.wallMode == "samlet") {
                    (d.wallTotalAreaM2 ?: 0f) - (d.openingTotalAreaM2 ?: 0f)
                } else {
                    (totalArea - openingsArea)
                }.coerceAtLeast(0f)

                val detailsMap = mapOf<String, Any>(
                    "isRepair" to (d.isRepair ?: false),
                    "murType" to (d.murType ?: ""),
                    "bearingWall" to (d.bearingWall ?: false),
                    "wallMeasurements" to d.wallMeasurements,
                    "netArea" to netArea,
                    "foundationOption" to (d.foundationOption ?: ""),
                    "goodAccess" to (d.goodAccess ?: false)
                )

                val request = Request(
                    userId = userId,
                    role = "private",
                    fag = "Murer",
                    category = "opmuring",
                    areaM2 = netArea,
                    roomType = d.murType ?: "Opmuring",
                    requiresMembrane = false,
                    aiPrice = aiPriceEstimate.value ?: 0f,
                    images = imageUris.value.map { it.toString() },
                    description = description.value,
                    status = "new"
                ).apply {
                    details = detailsMap
                }

                requestRepository.createRequest(request)
                Timber.d("Opgave sendt (opmuring)")
                onComplete()
            } catch (e: Exception) {
                Timber.e(e, "Send task fejl (opmuring): ${e.message}")
            } finally {
                setIsSending(false)
            }
        }
    }
}
