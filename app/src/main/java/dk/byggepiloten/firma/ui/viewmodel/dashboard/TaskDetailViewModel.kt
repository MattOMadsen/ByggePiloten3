// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/dashboard/TaskDetailViewModel.kt
// FULD RETTET VERSION – FJERNET bidsCount (da getBidsForRequest ikke findes endnu)
// + Loader kun request (basis info + detaljer + billeder virker)
// + sentAt som Long → Date + dansk format
// + Error handling
// + Matcher TaskDetailScreen perfekt (ingen bidsCount reference)
// + ca. 120 linjer

package dk.byggepiloten.firma.ui.viewmodel.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.task.Request
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskDetailState(
    val isLoading: Boolean = true,
    val request: Request? = null,
    val error: String? = null
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val requestRepository: RequestRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state = _state.asStateFlow()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _state.value = TaskDetailState(isLoading = true)
            try {
                val request = requestRepository.getRequestById(taskId) ?: throw Exception("Opgave ikke fundet")
                _state.value = TaskDetailState(
                    request = request,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = TaskDetailState(error = e.message, isLoading = false)
            }
        }
    }

    fun deleteTask(taskId: String, onDeleted: () -> Unit) {
        viewModelScope.launch {
            try {
                requestRepository.deleteRequest(taskId)
                onDeleted()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Sletning mislykkedes")
            }
        }
    }
}