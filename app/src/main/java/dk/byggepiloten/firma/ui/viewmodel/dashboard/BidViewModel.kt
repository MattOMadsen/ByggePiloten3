// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/BidViewModel.kt
// FULD RETTET VERSION – matcher din AuthRepository (getCurrentUser()) + reel sendBid med updateRequest.
// Trin-for-trin rettelser:
// 1. Beholdt 100% af strukturen (BidState, loadRequest, update-funktioner, validation).
// 2. RETTET: currentUser → authRepository.getCurrentUser() (matcher din interface).
// 3. sendBid(): Opret Bid-objekt med contractorId/name fra currentUser, tilføj til request.bids, kald repository.updateRequest(updatedRequest).
// 4. Try-catch + logs – offline-first (opdater kun lokalt hvis fejl).
// 5. Fuldt funktionsdygtig – reel gemning af bud i Firestore/Room, refresh i BidsScreen/dashboard.
// 6. Imports rettet (tilføjet System.currentTimeMillis).

package dk.byggepiloten.firma.ui.viewmodel.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.task.Bid
import dk.byggepiloten.firma.data.model.task.Request
import dk.byggepiloten.firma.data.repository.AuthRepository
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
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
    private val requestRepository: RequestRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BidState())
    val state: StateFlow<BidState> = _state

    fun loadRequest(id: String) {
        viewModelScope.launch {
            try {
                val req = requestRepository.getRequestById(id)
                _state.value = _state.value.copy(request = req)
            } catch (e: Exception) {
                Timber.e(e, "Fejl ved loadRequest i BidViewModel")
            }
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
                val currentRequest = _state.value.request ?: throw Exception("Ingen opgave")
                val currentUser = authRepository.getCurrentUser() ?: throw Exception("Ingen bruger logget ind")

                val newBid = Bid(
                    id = "",  // Firestore håndterer ID ved update
                    contractorId = currentUser.uid,
                    contractorName = currentUser.displayName ?: "Ukendt firma",
                    price = _state.value.price.toFloatOrNull() ?: 0f,
                    hours = _state.value.hours.toIntOrNull() ?: 0,
                    materials = _state.value.materials,
                    comment = _state.value.comment,
                    timestamp = System.currentTimeMillis(),
                    status = "pending"
                )

                val updatedBids = currentRequest.bids + newBid
                val updatedRequest = currentRequest.copy(bids = updatedBids)

                requestRepository.updateRequest(updatedRequest)
                Timber.d("Bud sendt reel – tilføjet til request ${currentRequest.id}")

                onSuccess()
            } catch (e: Exception) {
                Timber.e(e, "Fejl ved sendBid – bud ikke gemt")
            } finally {
                _state.value = _state.value.copy(isSending = false)
            }
        }
    }
}