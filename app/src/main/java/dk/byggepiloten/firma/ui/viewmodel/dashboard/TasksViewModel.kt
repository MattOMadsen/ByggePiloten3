// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/dashboard/TasksViewModel.kt
package dk.byggepiloten.firma.ui.viewmodel.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.task.Request
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class TasksState(
    val isLoading: Boolean = false,
    val requests: List<Request> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val requestRepository: RequestRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TasksState())
    val state = _state.asStateFlow()

    init {
        refreshTasks()
    }

    fun refreshTasks() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // Da getUserRequests() returnerer List<Request>? (en suspend funktion, ikke et Flow),
                // kalder vi den direkte og afventer resultatet.
                val requests = requestRepository.getUserRequests()
                
                _state.value = TasksState(
                    requests = requests ?: emptyList(),
                    isLoading = false,
                    error = null
                )
                Timber.d("TasksViewModel: Loaded ${requests?.size ?: 0} tasks")
                
            } catch (e: Exception) {
                _state.value = TasksState(
                    error = e.message,
                    isLoading = false,
                    requests = emptyList()
                )
                Timber.e(e, "TasksViewModel: Fejl ved hentning af opgaver")
            }
        }
    }
}
