package dk.byggepiloten.firma.ui.viewmodel.task

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.task.BadevaerelseData
import dk.byggepiloten.firma.data.model.task.Request
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BadevaerelseTaskViewModel @Inject constructor(
    requestRepository: RequestRepository
) : BaseTaskViewModel(requestRepository) {

    private val _badevaerelseData = MutableStateFlow(BadevaerelseData())
    val badevaerelseData = _badevaerelseData.asStateFlow()

    fun updateBadevaerelseData(data: BadevaerelseData) {
        _badevaerelseData.value = data
    }

    override fun sendTask(onComplete: () -> Unit) {
        viewModelScope.launch {
            setIsSending(true)
            try {
                val category = currentCategory.value ?: ""
                
                val d = _badevaerelseData.value
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("Ingen bruger")

                // Beregn et estimeret areal (gulv + vægge minus fradrag) hvis det findes
                val floorArea = (d.floorLength ?: 0f) * (d.floorWidth ?: 0f)
                val netArea = d.wallManualArea ?: floorArea

                val detailsMap = mapOf<String, Any>(
                    "renovationType" to (d.renovationType ?: ""),
                    "floorLength" to (d.floorLength ?: 0f),
                    "floorWidth" to (d.floorWidth ?: 0f),
                    "wallHeight" to (d.wallHeight ?: 0f),
                    "netArea" to netArea
                )

                val request = Request(
                    userId = userId,
                    role = "private",
                    fag = "Murer",
                    category = "badeværelse",
                    areaM2 = netArea,
                    roomType = "Badeværelse",
                    requiresMembrane = d.hasMembrane ?: true,
                    aiPrice = aiPriceEstimate.value ?: 0f,
                    images = imageUris.value.map { it.toString() },
                    description = description.value,
                    status = "new"
                ).apply {
                    details = detailsMap
                }

                requestRepository.createRequest(request)
                Timber.d("Opgave sendt (badeværelse)")
                onComplete()
            } catch (e: Exception) {
                Timber.e(e, "Send task fejl (badeværelse): ${e.message}")
            } finally {
                setIsSending(false)
            }
        }
    }
}
