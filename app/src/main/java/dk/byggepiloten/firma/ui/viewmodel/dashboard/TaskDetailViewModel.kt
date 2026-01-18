// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/dashboard/TaskDetailViewModel.kt
// FULD RETTET VERSION – tilføjet bidsCount, isContractor, isOwner
// + Load current user role fra users/{uid}
// + Load bids count fra requests/{taskId}/bids (subcollection)
// + Load isOwner (request.userId == currentUid)
// + Alle nødvendige imports + kommentarer
// + StateFlow for uiState med nye felter
// ca. 320 linjer (baseret på repo + nye felter)

package dk.byggepiloten.firma.ui.viewmodel.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.task.Request
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

data class TaskDetailState(
    val isLoading: Boolean = true,
    val request: Request? = null,
    val bidsCount: Int = 0,
    val isContractor: Boolean = false,
    val isOwner: Boolean = false
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state = _state.asStateFlow()

    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val currentUid = auth.currentUser?.uid
                if (currentUid == null) {
                    Timber.e("Ingen bruger logget ind")
                    return@launch
                }

                // Load request
                val requestDoc = firestore.collection("requests").document(taskId).get().await()
                val request = requestDoc.toObject(Request::class.java)

                if (request == null) {
                    Timber.w("Opgave ikke fundet: $taskId")
                    _state.value = _state.value.copy(isLoading = false)
                    return@launch
                }

                // Load bids count
                val bidsSnapshot = firestore.collection("requests")
                    .document(taskId)
                    .collection("bids")
                    .get()
                    .await()
                val bidsCount = bidsSnapshot.size()

                // Load current user role
                val userDoc = firestore.collection("users").document(currentUid).get().await()
                val userRole = userDoc.getString("role") ?: "PRIVATE"
                val isContractor = userRole == "CONTRACTOR"

                // Check owner
                val isOwner = request.userId == currentUid

                _state.value = TaskDetailState(
                    isLoading = false,
                    request = request,
                    bidsCount = bidsCount,
                    isContractor = isContractor,
                    isOwner = isOwner
                )
            } catch (e: Exception) {
                Timber.e(e, "Fejl ved load af opgave $taskId")
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}