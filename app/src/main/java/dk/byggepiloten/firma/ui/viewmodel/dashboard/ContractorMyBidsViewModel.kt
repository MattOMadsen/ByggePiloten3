// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/ContractorMyBidsViewModel.kt
// NY FIL – 128 linjer
// Formål: Loader alle requests og filtrerer contractor's egne bids (pending, accepted, declined).
// Offline-first via RequestRepository (Room cache + Firestore snapshot).
// Viser flad liste af BidWithRequest (custom data class for UI).

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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class BidWithRequest(
    val bid: Bid,
    val request: Request
)

@HiltViewModel
class ContractorMyBidsViewModel @Inject constructor(
    private val requestRepository: RequestRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _bids = MutableStateFlow<List<BidWithRequest>>(emptyList())
    val bids: StateFlow<List<BidWithRequest>> = _bids.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadMyBids()
    }

    fun refresh() = loadMyBids()

    private fun loadMyBids() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = authRepository.getCurrentUser() ?: run {
                    _isLoading.value = false
                    return@launch
                }

                requestRepository.getAllRequests().collect { allRequests ->
                    val myBids = allRequests.flatMap { request ->
                        request.bids.filter { it.contractorId == currentUser.uid }
                            .map { bid -> BidWithRequest(bid, request) }
                    }.sortedByDescending { it.bid.timestamp }

                    _bids.value = myBids
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Timber.e(e, "Fejl ved loadMyBids")
                _bids.value = emptyList()
                _isLoading.value = false
            }
        }
    }
}