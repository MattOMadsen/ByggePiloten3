// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/task/BaseTaskViewModel.kt
// FULD RETTET – setError gjort public
// Tilføjet manglende imports for collectAsStateWithLifecycle i screens (men denne fil har ingen)
// aiEstimateGenerator beholdt i constructor

package dk.byggepiloten.firma.ui.viewmodel.task

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

open class BaseTaskViewModel @Inject constructor(
    private val aiEstimateGenerator: AiEstimateGenerator
) : ViewModel() {

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    fun updateDescription(newText: String) {
        _description.value = newText
    }

    private val _imageUris = MutableStateFlow<List<Uri>>(emptyList())
    val imageUris: StateFlow<List<Uri>> = _imageUris.asStateFlow()

    fun addImages(uris: List<Uri>) {
        _imageUris.value = _imageUris.value + uris
    }

    fun removeImage(uri: Uri) {
        _imageUris.value = _imageUris.value - uri
    }

    fun updateImages(uris: List<Uri>) {
        _imageUris.value = uris
    }

    private val _aiPriceEstimate = MutableStateFlow<Long?>(null)
    val aiPriceEstimate: StateFlow<Long?> = _aiPriceEstimate.asStateFlow()

    private val _isGeneratingEstimate = MutableStateFlow(false)
    val isGeneratingEstimate: StateFlow<Boolean> = _isGeneratingEstimate.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    fun setIsSending(value: Boolean) {
        _isSending.value = value
    }

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Public så WizardScreen kan vise fejl som rød tekst
    fun setError(message: String?) {
        _error.value = message
    }

    fun clearError() {
        _error.value = null
    }

    private val _currentCategory = MutableStateFlow("")
    val currentCategory: StateFlow<String> = _currentCategory.asStateFlow()

    fun setCurrentCategory(category: String) {
        _currentCategory.value = category
    }

    open fun generateAiEstimate(areaM2: Float) {
        if (_isGeneratingEstimate.value) return

        viewModelScope.launch {
            _isGeneratingEstimate.value = true
            aiEstimateGenerator.generateEstimate(
                category = currentCategory.value,
                areaM2 = areaM2,
                description = description.value,
                onSuccess = { estimate ->
                    _aiPriceEstimate.value = estimate
                },
                onError = { msg ->
                    setError(msg)
                    _aiPriceEstimate.value = null
                }
            )
            _isGeneratingEstimate.value = false
        }
    }

    open fun sendTask(onComplete: () -> Unit) {
        viewModelScope.launch {
            setIsSending(true)
            try {
                onComplete()
            } catch (e: Exception) {
                Timber.e(e, "Send task fejl")
                setError("Kunne ikke sende opgaven")
            } finally {
                setIsSending(false)
            }
        }
    }
}