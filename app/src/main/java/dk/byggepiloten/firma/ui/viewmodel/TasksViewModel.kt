package dk.byggepiloten.firma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.Request
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class TasksState(
    val requests: List<Request> = emptyList(),
    val filter: String = "all",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val requestRepository: RequestRepository
) : ViewModel() {
    private val _state = MutableStateFlow(TasksState())
    val state: StateFlow<TasksState> = _state.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val requests = requestRepository.getUserRequests() ?: emptyList()
                _state.value = _state.value.copy(
                    requests = requests,
                    isLoading = false
                )
                Timber.d("Tasks: Loader ${requests.size} requests")
            } catch (e: Exception) {  // RETTET FIX: Udvidet try-catch for Room IllegalStateException (schema-mismatch) – sæt error, vis emptyList, undgå crash.
                Timber.e(e, "Load tasks fejl")
                val errorMsg = when {
                    e.message?.contains("Room cannot verify") == true -> "Database-fejl – genstart appen"
                    else -> "Hentning mislykkedes"
                }
                _state.value = _state.value.copy(isLoading = false, error = errorMsg, requests = emptyList())  // Fallback: Vis tom liste.
            }
        }
    }

    fun updateFilter(newFilter: String) {
        val allRequests = _state.value.requests
        val filtered = when (newFilter) {
            "new" -> allRequests.filter { (it.status ?: "new") == "new" }  // RETTET: Safe access med fallback – løser unresolved reference
            "completed" -> allRequests.filter { (it.status ?: "new") == "completed" }  // RETTET: Safe access med fallback – løser unresolved reference
            else -> allRequests
        }
        _state.value = _state.value.copy(filter = newFilter, requests = filtered)
    }

    fun deleteTask(taskId: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                requestRepository.deleteRequest(taskId)
                loadTasks()
                onComplete(true)
                Timber.d("Tasks: Slettet task $taskId")
            } catch (e: Exception) {
                Timber.e(e, "Delete task fejl")
                _state.value = _state.value.copy(error = "Sletning mislykkedes")
                onComplete(false)
            }
        }
    }

    fun refreshTasks() {
        viewModelScope.launch {  // BEHOLDT: Wrap i launch for coroutine – matcher loadTasks
            loadTasks()
        }
    }
}