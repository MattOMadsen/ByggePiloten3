package dk.byggepiloten.firma.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.Request
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class TaskState(
    val description: String = "",
    val imageUris: List<Uri> = emptyList(),
    val aiPriceEstimate: Float? = null,
    val isSending: Boolean = false
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val requestRepository: RequestRepository  // NY: Injektion for sendTask – matcher RequestRepository i di
) : ViewModel() {
    private val _state = MutableStateFlow(TaskState())
    val state = _state.asStateFlow()

    fun addImages(uris: List<Uri>) {
        _state.value = _state.value.copy(imageUris = _state.value.imageUris + uris)
    }

    fun removeImage(uri: Uri) {
        _state.value = _state.value.copy(imageUris = _state.value.imageUris - uri)
    }

    fun updateDescription(description: String) {
        _state.value = _state.value.copy(description = description)
    }

    // OP: Gjort suspend med coroutine.launch og repo-kald – implementerer afsendelse (placeholder userId, fag etc. – udvid senere)
    // Brug viewModelScope for asynkron håndtering uden blokering af UI
    fun sendTask(onComplete: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSending = true)
            try {
                val request = Request(
                    userId = "current_user_placeholder",  // Hent fra AuthManager senere
                    role = "private",  // Fra DataStore
                    fag = "Murer",  // Placeholder – hent fra UI-valg
                    category = "Placeholder category",  // Hent fra UI
                    areaM2 = 10f,  // Placeholder – beregn fra UI
                    roomType = "Badeværelse",  // Placeholder
                    requiresMembrane = false,
                    aiPrice = state.value.aiPriceEstimate ?: 0f,
                    images = state.value.imageUris.map { it.toString() },  // Konverter Uri til String (URL/path)
                    description = state.value.description,  // NY: Brug nyt felt
                    status = "new"  // NY: Brug nyt felt
                )
                requestRepository.createRequest(request)
                Timber.d("TaskViewModel: Opgave sendt – ID: ${request.id}")
                onComplete()
            } catch (e: Exception) {
                Timber.e(e, "Send task fejl")
                // TODO: Tilføj error-state hvis nødvendigt
            } finally {
                _state.value = _state.value.copy(isSending = false)
            }
        }
    }
}