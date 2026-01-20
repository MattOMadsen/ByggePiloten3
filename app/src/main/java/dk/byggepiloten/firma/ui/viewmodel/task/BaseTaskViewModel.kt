// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/task/BaseTaskViewModel.kt
// FIX: Protected setters + extraDetails i generateAiEstimate

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

    protected fun setAiPriceEstimate(value: Long?) {
        _aiPriceEstimate.value = value
    }

    private val _isGeneratingEstimate = MutableStateFlow(false)
    val isGeneratingEstimate: StateFlow<Boolean> = _isGeneratingEstimate.asStateFlow()

    protected fun setIsGeneratingEstimate(value: Boolean) {
        _isGeneratingEstimate.value = value
    }

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    fun setIsSending(value: Boolean) {
        _isSending.value = value
    }

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

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

    open fun generateAiEstimate(areaM2: Float, extraDetails: String? = null) {
        if (_isGeneratingEstimate.value) return

        viewModelScope.launch {
            setIsGeneratingEstimate(true)
            clearError()
            aiEstimateGenerator.generateEstimate(
                category = currentCategory.value,
                areaM2 = areaM2,
                description = description.value.takeIf { it.isNotBlank() },
                extraDetails = extraDetails,
                onSuccess = { estimate ->
                    setAiPriceEstimate(estimate)
                    clearError()
                },
                onError = { msg ->
                    setError(msg)
                    setAiPriceEstimate(null)
                }
            )
            setIsGeneratingEstimate(false)
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