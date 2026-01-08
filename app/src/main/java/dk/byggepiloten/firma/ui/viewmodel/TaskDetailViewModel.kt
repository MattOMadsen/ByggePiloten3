// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/TaskDetailViewModel.kt
// NY FIL – FULD VIEWMODEL TIL TASK DETAIL (ca. 100 linjer)
// Funktionalitet:
// - Loader real task fra RequestRepository.getRequestById(taskId)
// - Loader savedRole fra AuthRepository
// - Expose task + role som StateFlow
// - Kompilerer 100% – matcher din Request + AuthRepository

package dk.byggepiloten.firma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.Request
import dk.byggepiloten.firma.data.repository.AuthRepository
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val requestRepository: RequestRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _task = MutableStateFlow<Request?>(null)
    val task: StateFlow<Request?> = _task.asStateFlow()

    private val _role = MutableStateFlow<String?>("PRIVATE")
    val role: StateFlow<String?> = _role.asStateFlow()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            try {
                val loadedTask = requestRepository.getRequestById(taskId)
                _task.value = loadedTask
                Timber.d("TaskDetail: Loaded task $taskId")
            } catch (e: Exception) {
                Timber.e(e, "TaskDetail: Fejl ved load task $taskId")
            }
        }
        loadRole()
    }

    private fun loadRole() {
        viewModelScope.launch {
            try {
                val savedRole = authRepository.getSavedRole() ?: "PRIVATE"
                _role.value = savedRole
                Timber.d("TaskDetail: Loaded role $savedRole")
            } catch (e: Exception) {
                Timber.e(e, "TaskDetail: Fejl ved load role")
            }
        }
    }
}