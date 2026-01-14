// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/task/OpmuringTaskViewModel.kt
// OPDATERET – Tilføjet isStepValid-funktion (validering pr. step)
// Bruges til at disable "Næste" hvis step ikke er gyldigt (f.eks. areal > 0, obligatoriske fotos)
// Linjer: 312

package dk.byggepiloten.firma.ui.viewmodel.task

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.task.Request
import dk.byggepiloten.firma.data.model.task.WallData
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OpmuringTaskViewModel @Inject constructor(
    private val requestRepository: RequestRepository
) : BaseTaskViewModel() {

    private val _wallData = MutableStateFlow(WallData())
    val wallData = _wallData.asStateFlow()

    private val _stepPhotos = MutableStateFlow<Map<String, List<Uri>>>(emptyMap())
    val stepPhotos = _stepPhotos.asStateFlow()

    fun updateWallData(data: WallData) {
        _wallData.value = data
    }

    fun updateStepPhotos(stepId: String, uris: List<Uri>) {
        _stepPhotos.value = _stepPhotos.value.toMutableMap().apply { this[stepId] = uris }
    }

    // Validering pr. step – udvides efter behov
    fun isStepValid(stepNumber: Int): Boolean {
        val d = _wallData.value
        val photos = _stepPhotos.value

        return when (stepNumber) {
            1 -> d.murType != null // Murtype valgt
            2 -> d.isRepair != null // Ny eller reparation
            3 -> d.bearingWall != null // Bærende væg
            4 -> {
                // Dimensioner: areal > 0
                if (d.wallMode == "samlet") {
                    (d.wallTotalAreaM2 ?: 0f) > 0f
                } else {
                    d.wallMeasurements.isNotEmpty() && d.wallMeasurements.all {
                        (it.length ?: 0f) > 0f && (it.height ?: 0f) > 0f
                    }
                }
            }
            8 -> {
                // Åbninger: hvis ikke "ingen", så areal eller målinger udfyldt
                if (d.openingMode == null) true
                else if (d.openingMode == "samlet") (d.openingTotalAreaM2 ?: 0f) > 0f
                else d.openingMeasurements.isNotEmpty()
            }
            12 -> d.foundationOption != null // Fundament valgt
            13 -> {
                // Skader: hvis ja til skade, så fotos uploaded
                if (d.hasCracks != true && d.hasMoistureDamage != true && d.hasSettlementDamage != true) true
                else (photos["damage"] ?: emptyList()).isNotEmpty()
            }
            14 -> {
                // Adgang: hvis nej, så problemer valgt + fotos
                if (d.goodAccess != false) true
                else d.accessProblems.isNotEmpty() && (photos["access"] ?: emptyList()).isNotEmpty()
            }
            else -> true // Andre steps (tykkelse, sten osv.) valideres senere hvis nødvendigt
        }
    }

    override fun sendTask(onComplete: () -> Unit) {
        viewModelScope.launch {
            setIsSending(true)
            try {
                val d = _wallData.value
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("Ingen bruger")
                val requestId = UUID.randomUUID().toString()

                val totalArea = d.wallMeasurements.sumOf { (it.length ?: 0f).toDouble() * (it.height ?: 0f).toDouble() }.toFloat()
                val openingsArea = d.openingMeasurements.sumOf { (it.widthCm ?: 0f).toDouble() * (it.heightCm ?: 0f).toDouble() / 10000.0 }.toFloat()

                val netArea = if (d.wallMode == "samlet") {
                    (d.wallTotalAreaM2 ?: 0f) - (d.openingTotalAreaM2 ?: 0f)
                } else {
                    (totalArea - openingsArea)
                }.coerceAtLeast(0f)

                val storage = FirebaseStorage.getInstance()
                val labeledUrls = mutableMapOf<String, List<String>>()

                _stepPhotos.value.forEach { (stepId, uris) ->
                    val urls = uris.mapNotNull { uri ->
                        try {
                            val ref = storage.reference.child("requests/$requestId/$stepId/${UUID.randomUUID()}")
                            ref.putFile(uri).await()
                            ref.downloadUrl.await().toString()
                        } catch (e: Exception) {
                            Timber.e(e, "Upload fejl $stepId")
                            null
                        }
                    }
                    if (urls.isNotEmpty()) labeledUrls["photos_$stepId"] = urls
                }

                val generalUrls = imageUris.value.mapNotNull { uri ->
                    try {
                        val ref = storage.reference.child("requests/$requestId/general/${UUID.randomUUID()}")
                        ref.putFile(uri).await()
                        ref.downloadUrl.await().toString()
                    } catch (e: Exception) {
                        Timber.e(e, "Upload fejl general")
                        null
                    }
                }

                val detailsMap = mapOf<String, Any>(
                    "isRepair" to (d.isRepair ?: false),
                    "murType" to (d.murType ?: ""),
                    "bearingWall" to (d.bearingWall ?: false),
                    "wallMeasurements" to d.wallMeasurements,
                    "netArea" to netArea,
                    "foundationOption" to (d.foundationOption ?: ""),
                    "goodAccess" to (d.goodAccess ?: false),
                    "accessProblems" to (d.accessProblems ?: emptyList()),
                    "accessCustomDescription" to (d.accessCustomDescription ?: ""),
                    "labeledPhotos" to labeledUrls
                )

                val request = Request(
                    userId = userId,
                    role = "private",
                    fag = "Murer",
                    category = "opmuring",
                    areaM2 = netArea,
                    roomType = d.murType ?: "Opmuring",
                    requiresMembrane = false,
                    aiPrice = (aiPriceEstimate.value ?: 0L).toFloat(),
                    images = generalUrls + labeledUrls.values.flatten(),
                    description = description.value,
                    status = "new"
                ).apply {
                    details = detailsMap
                }

                requestRepository.createRequest(request)
                Timber.d("Opgave sendt med labeled photos")
                onComplete()
            } catch (e: Exception) {
                Timber.e(e, "Send task fejl: ${e.message}")
            } finally {
                setIsSending(false)
            }
        }
    }
}