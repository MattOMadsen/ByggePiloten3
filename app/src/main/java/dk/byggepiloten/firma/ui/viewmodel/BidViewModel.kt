package dk.byggepiloten.firma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.Request
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BidState(
    val request: Request? = null,
    val price: String = "",
    val hours: String = "",
    val materials: String = "",
    val comment: String = "",
    val isSending: Boolean = false,
    val isValid: Boolean = false
)

@HiltViewModel
class BidViewModel @Inject constructor(
    private val requestRepository: RequestRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BidState())
    val state: StateFlow<BidState> = _state

    fun loadRequest(id: String) {
        viewModelScope.launch {
            val req = requestRepository.getRequestById(id)
            _state.value = _state.value.copy(request = req)
        }
    }

    fun updatePrice(text: String) {
        _state.value = _state.value.copy(price = text)
        validate()
    }

    fun updateHours(text: String) {
        _state.value = _state.value.copy(hours = text)
        validate()
    }

    fun updateMaterials(text: String) {
        _state.value = _state.value.copy(materials = text)
    }

    fun updateComment(text: String) {
        _state.value = _state.value.copy(comment = text)
    }

    private fun validate() {
        val price = _state.value.price.toFloatOrNull() ?: 0f
        val hours = _state.value.hours.toIntOrNull() ?: 0
        _state.value = _state.value.copy(isValid = price > 1000 && hours > 0)
    }

    fun sendBid(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSending = true)
            try {
                // TODO: Gem bud i Firestore når Bid-model findes
                onSuccess()
            } finally {
                _state.value = _state.value.copy(isSending = false)
            }
        }
    }
}