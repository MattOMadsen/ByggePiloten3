// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/task/OpmuringTaskViewModel.kt
// FIX: Billeder valgfrit (fjernet missing.add(15))

package dk.byggepiloten.firma.ui.viewmodel.task

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.task.WallData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OpmuringTaskViewModel @Inject constructor(
    aiEstimateGenerator: AiEstimateGenerator,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : BaseTaskViewModel(aiEstimateGenerator) {

    private val _wallData = MutableStateFlow(WallData())
    val wallData: StateFlow<WallData> = _wallData.asStateFlow()

    private val _stepPhotos = MutableStateFlow<Map<String, List<Uri>>>(emptyMap())
    val stepPhotos: StateFlow<Map<String, List<Uri>>> = _stepPhotos.asStateFlow()

    fun updateWallDataDirect(data: WallData) {
        _wallData.value = data
    }

    fun updateStepPhotos(step: String, uris: List<Uri>) {
        _stepPhotos.value = _stepPhotos.value.toMutableMap().apply { put(step, uris) }
    }

    fun calculateAndGenerateEstimate() {
        val data = wallData.value
        val area = if (data.wallMode == "Samlet areal") {
            data.wallTotalAreaM2 ?: 0f
        } else {
            data.wallMeasurements.sumOf { ((it.length ?: 0f) * (it.height ?: 0f)).toDouble() }.toFloat()
        }
        val openingArea = data.openingMeasurements.sumOf {
            ((it.widthCm ?: 0f) / 100f * (it.heightCm ?: 0f) / 100f).toDouble()
        }.toFloat()
        val nettoArea = (area - openingArea).coerceAtLeast(0f)

        val extraDetails = buildString {
            append("Type mur: ${data.murType ?: "ukendt"}")
            if (data.isRepair == true) append(", reparation")
            if (data.bearingWall == true) append(", bærende væg")
            data.thicknessOption?.let { append(", tykkelse: $it") }
            data.stoneType?.let { append(", sten: $it") }
            data.surfaceFinish?.let { append(", overflade: $it") }
            if (data.reinforcementLevel != null && data.reinforcementLevel != "none") append(", armering: ja")
            if (data.insulationWanted == true) append(", isolering: ja")
            if (data.hasCracks == true || data.hasMoistureDamage == true || data.hasSettlementDamage == true) append(", skader rapporteret")
            if (data.goodAccess == false) append(", begrænset adgang")
        }.takeIf { it.isNotBlank() } ?: "Standard opmuring uden særlige detaljer"

        super.generateAiEstimate(nettoArea, extraDetails)
    }

    fun validateBeforeSend(): List<Int> {
        val data = wallData.value
        val missing = mutableListOf<Int>()

        if (data.murType == null) missing.add(1)
        if (data.isRepair == null) missing.add(2)
        if (data.bearingWall == null) missing.add(3)

        val hasArea = data.wallTotalAreaM2 != null && data.wallTotalAreaM2!! > 0f ||
                data.wallMeasurements.isNotEmpty() && data.wallMeasurements.all { it.length != null && it.height != null && (it.length!! > 0f) && (it.height!! > 0f) }
        if (!hasArea) missing.add(4)

        if (data.thicknessOption.isNullOrBlank() && data.customThickness == null) missing.add(5)
        if (data.stoneType.isNullOrBlank()) missing.add(6)
        if (data.mortarType.isNullOrBlank()) missing.add(7)
        if (data.surfaceFinish.isNullOrBlank()) missing.add(9)

        val needsArmering = data.surfaceFinish.orEmpty().lowercase().let {
            it.contains("puds") || it.contains("malet") || it.contains("filt") ||
                    it.contains("skalcem") || it.contains("dura") || it.contains("vandskuring")
        }
        if (needsArmering && data.reinforcementLevel == null) missing.add(10)

        // Billeder er nu valgfrit – ingen check

        return missing.sorted()
    }

    override fun sendTask(onComplete: () -> Unit) {
        val missingSteps = validateBeforeSend()
        if (missingSteps.isNotEmpty()) {
            setError("Manglende trin – udfyld venligst alle påkrævede felter")
            return
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            setError("Du skal være logget ind for at sende opgave")
            return
        }

        viewModelScope.launch {
            setIsSending(true)
            setError(null)
            try {
                val data = wallData.value
                val grossArea = if (data.wallMode == "Samlet areal") {
                    data.wallTotalAreaM2 ?: 0f
                } else {
                    data.wallMeasurements.sumOf { ((it.length ?: 0f) * (it.height ?: 0f)).toDouble() }.toFloat()
                }
                val openingArea = data.openingMeasurements.sumOf {
                    ((it.widthCm ?: 0f) / 100f * (it.heightCm ?: 0f) / 100f).toDouble()
                }.toFloat()
                val nettoAreaM2 = (grossArea - openingArea).coerceAtLeast(0f)

                val allUris = imageUris.value + stepPhotos.value.values.flatten()
                val imageUrls = mutableListOf<String>()

                val taskRef = firestore.collection("requests").document()
                val taskId = taskRef.id

                if (allUris.isNotEmpty()) {
                    allUris.forEach { uri ->
                        val fileName = uri.lastPathSegment ?: System.currentTimeMillis().toString()
                        val imageRef = storage.reference.child("requests/$taskId/images/$fileName")
                        val uploadTask = imageRef.putFile(uri).await()
                        val downloadUrl = uploadTask.metadata?.reference?.downloadUrl?.await()?.toString()
                        downloadUrl?.let { imageUrls.add(it) }
                    }
                }

                val currentTime = System.currentTimeMillis()

                val taskData = hashMapOf<String, Any?>(
                    "userId" to currentUser.uid,
                    "category" to "opmuring",
                    "areaM2" to nettoAreaM2,
                    "roomType" to (data.murType ?: "Opmuring"),
                    "aiPrice" to (aiPriceEstimate.value ?: 0L),
                    "images" to imageUrls,
                    "description" to description.value,
                    "status" to "new",
                    "details" to wallData.value.toMap(),
                    "createdAt" to currentTime,
                    "sentAt" to currentTime
                )

                taskRef.set(taskData).await()

                Timber.d("Opgave sendt succesfuldt – ID: $taskId")
                onComplete()
            } catch (e: Exception) {
                Timber.e(e, "Fejl ved send opgave")
                setError("Kunne ikke sende opgaven – tjek internetforbindelse")
            } finally {
                setIsSending(false)
            }
        }
    }
}