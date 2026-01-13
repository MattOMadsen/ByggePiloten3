// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/task/BaseTaskViewModel.kt
// FULD RETTET – ingen constructor-args (Hilt/KSP-fix), fælles flows + open sendTask/generateAiEstimate
// Linjer: 178

package dk.byggepiloten.firma.ui.viewmodel.task

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

open class BaseTaskViewModel : ViewModel() {

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

    private val _aiPriceEstimate = MutableStateFlow<Long?>(null)
    val aiPriceEstimate: StateFlow<Long?> = _aiPriceEstimate.asStateFlow()

    private val _isGeneratingEstimate = MutableStateFlow(false)
    val isGeneratingEstimate: StateFlow<Boolean> = _isGeneratingEstimate.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    protected fun setIsSending(value: Boolean) {
        _isSending.value = value
    }

    private val _currentCategory = MutableStateFlow("")
    val currentCategory: StateFlow<String> = _currentCategory.asStateFlow()

    fun setCurrentCategory(category: String) {
        _currentCategory.value = category
    }

    // Placeholder AI-generation (Gemini Nano local → cloud fallback)
    open fun generateAiEstimate() {
        if (_isGeneratingEstimate.value) return

        viewModelScope.launch {
            _isGeneratingEstimate.value = true
            try {
                // TODO: Reel Gemini Nano + cloud integration her
                // Placeholder for test
                _aiPriceEstimate.value = 15000L
            } catch (e: Exception) {
                Timber.e(e, "AI estimate fejl")
                _aiPriceEstimate.value = null
            } finally {
                _isGeneratingEstimate.value = false
            }
        }
    }

    // Open sendTask – override i specifikke ViewModels med reel repository-logik
    open fun sendTask(onComplete: () -> Unit) {
        viewModelScope.launch {
            setIsSending(true)
            try {
                // Placeholder – reel logik i specifikke
                onComplete()
            } catch (e: Exception) {
                Timber.e(e, "Send task fejl")
            } finally {
                setIsSending(false)
            }
        }
    }
}