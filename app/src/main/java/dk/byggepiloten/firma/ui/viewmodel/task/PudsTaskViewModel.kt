// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/task/PudsTaskViewModel.kt
// FULD RETTET VERSION – matcher BaseTaskViewModel præcis
// Tilføjet override på sendTask
// Bruger setIsSending (korrekt navn fra base)
// Matcher Opmuring's sendTask-logik (lambda onSuccess)

package dk.byggepiloten.firma.ui.viewmodel.task

import android.net.Uri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.task.PudsData
import dk.byggepiloten.firma.ui.screen.new_task.categories.puds.PudsValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PudsTaskViewModel @Inject constructor(
    aiEstimateGenerator: AiEstimateGenerator
) : BaseTaskViewModel(aiEstimateGenerator) {

    private val _pudsData = MutableStateFlow(PudsData())
    val pudsData: StateFlow<PudsData> = _pudsData.asStateFlow()

    private val _stepPhotos = MutableStateFlow<Map<String, List<Uri>>>(emptyMap())
    val stepPhotos: StateFlow<Map<String, List<Uri>>> = _stepPhotos.asStateFlow()

    fun updatePudsData(newData: PudsData) {
        _pudsData.value = newData
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
                // TODO: reel task-save her når repository er klar
                onSuccess()
            } catch (e: Exception) {
                setError("Fejl ved afsendelse: ${e.message}")
            } finally {
                setIsSending(false)
            }
        }
    }

    override fun generateAiEstimate(areaM2: Float, extraDetails: String?) {
        val pudsDetails = extraDetails ?: "Pudsning ${pudsData.value.indeUde?.lowercase() ?: ""}"
        super.generateAiEstimate(areaM2, pudsDetails)
    }
}