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

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val requestRepository: RequestRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _task = MutableStateFlow<Request?>(null)
    val task: StateFlow<Request?> = _task.asStateFlow()

    private val _role = MutableStateFlow<String>("PRIVATE")
    val role: StateFlow<String> = _role.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _task.value = requestRepository.getRequestById(taskId)
            Timber.Forest.d("TaskDetail: Loaded task $taskId")

            val savedRole = authRepository.getSavedRole() ?: "PRIVATE"
            _role.value = savedRole
            Timber.Forest.d("TaskDetail: Loaded role $savedRole")
        }
    }

    fun showDeleteConfirmation() {
        _showDeleteDialog.value = true
    }

    fun dismissDeleteDialog() {
        _showDeleteDialog.value = false
    }

    fun deleteTask(taskId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                requestRepository.deleteRequest(taskId)
                Timber.Forest.d("TaskDetail: Slettet task $taskId")
                onSuccess()
            } catch (e: Exception) {
                Timber.Forest.e(e, "TaskDetail: Fejl ved sletning")
                dismissDeleteDialog()
            }
        }
    }
}