package dk.byggepiloten.firma.ui.viewmodel.task

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

open class BaseTaskViewModel @Inject constructor(
    protected val requestRepository: RequestRepository // ÆNDRET til protected
) : ViewModel() {

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _imageUris = MutableStateFlow<List<Uri>>(emptyList())
    val imageUris = _imageUris.asStateFlow()

    private val _aiPriceEstimate = MutableStateFlow<Float?>(null)
    val aiPriceEstimate = _aiPriceEstimate.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending = _isSending.asStateFlow()

    protected fun setIsSending(value: Boolean) { // NY – så subklasser kan sætte værdi
        _isSending.value = value
    }

    private val _isGeneratingEstimate = MutableStateFlow(false)
    val isGeneratingEstimate = _isGeneratingEstimate.asStateFlow()

    private val _currentCategory = MutableStateFlow<String?>("")
    val currentCategory = _currentCategory.asStateFlow()

    fun setCurrentCategory(category: String) {
        _currentCategory.value = category
    }

    fun updateDescription(description: String) {
        _description.value = description
    }

    fun addImages(uris: List<Uri>) {
        _imageUris.value = _imageUris.value + uris
    }

    fun removeImage(uri: Uri) {
        _imageUris.value = _imageUris.value.filter { it != uri }
    }

    fun setAiPriceEstimate(price: Float?) {
        _aiPriceEstimate.value = price
    }

    fun setGeneratingEstimate(generating: Boolean) {
        _isGeneratingEstimate.value = generating
    }

    // Skeleton – overrides i specifikke ViewModels
    open fun sendTask(onComplete: () -> Unit) {
        viewModelScope.launch {
            setIsSending(true)
            try {
                // Implementeres i subklasser
                onComplete()
            } catch (e: Exception) {
                Timber.Forest.e(e, "Send task fejl i base")
            } finally {
                setIsSending(false)
            }
        }
    }
}