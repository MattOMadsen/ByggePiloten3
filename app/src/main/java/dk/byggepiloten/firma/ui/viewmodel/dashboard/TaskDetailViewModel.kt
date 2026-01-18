// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/dashboard/TaskDetailViewModel.kt
// OPDATERET – baseret på repo-version (ca. 320 linjer original)
// + Tilføjet reel deleteTask med callback (sletter bids subcollection først, derefter request)
// + Tilføjet isDeleting + deleteError i state
// + Alle imports + kommentarer
// Ca. 400 linjer

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
    val isOwner: Boolean = false,
    val isDeleting: Boolean = false,
    val deleteError: String? = null
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state = _state.asStateFlow()

    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, deleteError = null)
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

    fun deleteTask(taskId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isDeleting = true, deleteError = null)
            try {
                // Batch delete bids + request
                val batch = firestore.batch()

                val bidsSnapshot = firestore.collection("requests")
                    .document(taskId)
                    .collection("bids")
                    .get()
                    .await()

                for (doc in bidsSnapshot.documents) {
                    batch.delete(doc.reference)
                }

                batch.delete(firestore.collection("requests").document(taskId))
                batch.commit().await()

                onSuccess()
            } catch (e: Exception) {
                Timber.e(e, "Fejl ved sletning af opgave $taskId")
                val errorMsg = e.message ?: "Ukendt fejl"
                _state.value = _state.value.copy(deleteError = errorMsg)
                onError(errorMsg)
            } finally {
                _state.value = _state.value.copy(isDeleting = false)
            }
        }
    }
}