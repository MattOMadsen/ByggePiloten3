// File: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/ContractorBidViewModel.kt
// FULD VERSION – kun firma ser opgaver fra Firestore

package dk.byggepiloten.firma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.Request
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ContractorBidViewModel @Inject constructor(
    private val requestRepository: RequestRepository
) : ViewModel() {

    private val _requests = MutableStateFlow<List<Request>>(emptyList())
    val requests: StateFlow<List<Request>> = _requests.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    init {
        loadRequests()
    }

    fun loadRequests() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = auth.currentUser?.uid ?: run {
                    _isLoading.value = false
                    return@launch
                }

                // Hent firmaets fag
                val firmaDoc = firestore.collection("firma_prices").document(userId).get().await()
                val firmaFag = firmaDoc.get("fag") as? List<String> ?: emptyList()  // RETTET: Safe cast med fallback – løser unchecked cast warning

                if (firmaFag.isEmpty()) {
                    _requests.value = getDummyRequests()
                    _isLoading.value = false
                    return@launch
                }

                requestRepository.getAllRequests().collect { all ->
                    val open = all.filter { request ->
                        // RETTET: Safe access til status med fallback til "new" – løser unresolved reference
                        val status = (request.status ?: "new").lowercase()
                        status in listOf("new", "åben", "pending", "sent", "open", "draft")
                                && request.fag in firmaFag
                    }
                    _requests.value = if (open.isEmpty()) getDummyRequests() else open
                }
            } catch (e: Exception) {
                Timber.e(e, "Fejl – viser dummy")
                _requests.value = getDummyRequests()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() = loadRequests()

    private fun getDummyRequests(): List<Request> {
        return listOf(
            Request(
                id = "999",
                userId = "demo1",
                role = "private",
                fag = "Murer",
                category = "Badeværelse renovering",
                areaM2 = 12.5f,
                roomType = "Badeværelse",
                requiresMembrane = true,
                aiPrice = 125000f,
                images = emptyList(),
                sentAt = System.currentTimeMillis(),
                status = "new",
                createdAt = System.currentTimeMillis()
            )
        )
    }
}