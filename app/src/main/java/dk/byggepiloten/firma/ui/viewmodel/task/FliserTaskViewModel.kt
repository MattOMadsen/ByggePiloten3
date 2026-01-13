package dk.byggepiloten.firma.ui.viewmodel.task

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.task.FliserData
import dk.byggepiloten.firma.data.model.task.Request
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth

@HiltViewModel
class FliserTaskViewModel @Inject constructor(
    requestRepository: RequestRepository
) : BaseTaskViewModel(requestRepository) {

    private val _fliserData = MutableStateFlow(FliserData())
    val fliserData = _fliserData.asStateFlow()

    fun updateFliserData(data: FliserData) {
        _fliserData.value = data
    }

    override fun sendTask(onComplete: () -> Unit) {
        viewModelScope.launch {
            setIsSending(true)
            try {
                val category = currentCategory.value ?: "flise_klinke"
                val d = _fliserData.value
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("Ingen bruger")

                // Beregn netto areal
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

                val request = Request(
                    userId = userId,
                    role = "private",
                    fag = "Murer",
                    category = "flise_klinke",
                    areaM2 = netArea,
                    roomType = "Flise- og klinkearbejde",
                    requiresMembrane = false,
                    aiPrice = aiPriceEstimate.value ?: 0f,
                    images = imageUris.value.map { it.toString() },
                    description = description.value,
                    status = "new"
                ).apply {
                    details = detailsMap
                }

                requestRepository.createRequest(request)
                Timber.d("Opgave sendt (flise_klinke)")
                onComplete()
            } catch (e: Exception) {
                Timber.e(e, "Send task fejl (flise_klinke): ${e.message}")
            } finally {
                setIsSending(false)
            }
        }
    }
}
