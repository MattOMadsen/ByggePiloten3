// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/dashboard/TaskDetailViewModel.kt
// OPDATERET VERSION – BASERET PÅ DIN EKSTERNE FIL
// Nu med samlet TaskDetailState (isLoading, request, error)
// Matcher TaskDetailScreen perfekt
// Bevarer repository-kald, role, delete-dialog og delete-logik
// ca. 110 linjer

package dk.byggepiloten.firma.ui.viewmodel.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.task.Request
import dk.byggepiloten.firma.data.repository.AuthRepository
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class TaskDetailState(
    val isLoading: Boolean = true,
    val request: Request? = null,
    val role: String = "PRIVATE",
    val showDeleteDialog: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val requestRepository: RequestRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state: StateFlow<TaskDetailState> = _state.asStateFlow()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val request = requestRepository.getRequestById(taskId)
                val savedRole = authRepository.getSavedRole() ?: "PRIVATE"

                _state.value = TaskDetailState(
                    isLoading = false,
                    request = request,
                    role = savedRole,
                    showDeleteDialog = false
                )

                Timber.d("TaskDetail: Loaded task $taskId, role $savedRole")
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Kunne ikke hente opgaven: ${e.localizedMessage}"
                )
                Timber.e(e, "TaskDetail: Fejl ved load af task $taskId")
            }
        }
    }

    fun showDeleteConfirmation() {
        _state.value = _state.value.copy(showDeleteDialog = true)
    }

    fun dismissDeleteDialog() {
        _state.value = _state.value.copy(showDeleteDialog = false)
    }

    fun deleteTask(taskId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                requestRepository.deleteRequest(taskId)
                Timber.d("TaskDetail: Slettet task $taskId")

                // Opdater state: fjern request og skjul dialog
                _state.value = _state.value.copy(
                    request = null,
                    showDeleteDialog = false
                )
                onSuccess()
            } catch (e: Exception) {
                Timber.e(e, "TaskDetail: Fejl ved sletning af task $taskId")
                _state.value = _state.value.copy(
                    showDeleteDialog = false,
                    error = "Kunne ikke slette opgaven"
                )
            }
        }
    }
}