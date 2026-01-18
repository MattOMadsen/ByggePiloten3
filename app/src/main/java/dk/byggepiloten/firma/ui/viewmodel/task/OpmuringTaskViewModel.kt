// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/task/OpmuringTaskViewModel.kt
// FULD OPDATERET – Tilføjet calculateAndGenerateEstimate()
// Beregner netto areal (væg - åbninger) og kalder generateAiEstimate fra BaseTaskViewModel
// Beholdt al eksisterende upload-logik og detailsMap (ingen sletning)
// Reel nettoArea bruges til AI-kald
// RETTET: WallMeasurement felter (length/height) og sumOf ambiguitet.
// RETTET: Tilføjet aiEstimateGenerator til constructor.

package dk.byggepiloten.firma.ui.viewmodel.task

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
    private val requestRepository: RequestRepository,
    private val application: Application,
    aiEstimateGenerator: AiEstimateGenerator
) : BaseTaskViewModel(aiEstimateGenerator) {

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

    /**
     * Beregner netto areal og genererer AI-estimat.
     * Kaldes automatisk i summary-step.
     */
    fun calculateAndGenerateEstimate() {
        val d = _wallData.value

        // Beregn vægareal fra individuelle målinger
        val wallArea = d.wallMeasurements.sumOf { ((it.length ?: 0f) * (it.height ?: 0f)).toDouble() }.toFloat()

        // Beregn åbningsareal fra individuelle åbninger
        val openingArea = d.openingMeasurements.sumOf { ((it.widthCm ?: 0f) / 100f * (it.heightCm ?: 0f) / 100f).toDouble() }.toFloat()

        val nettoArea = (wallArea - openingArea).coerceAtLeast(0f)

        if (nettoArea > 0f) {
            generateAiEstimate(nettoArea)
        } else {
            // Fallback hvis areal ikke kan beregnes
            generateAiEstimate(10f) // Minimal værdi for at få et estimat
        }
    }

    override fun sendTask(onComplete: () -> Unit) {
        viewModelScope.launch {
            setIsSending(true)
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser ?: run {
                    setError("Du skal være logget ind")
                    return@launch
                }
                val d = _wallData.value
                val descText = description.value

                val firestore = Firebase.firestore
                val docRef = firestore.collection("requests").document()
                val requestId = docRef.id

                val storage = FirebaseStorage.getInstance().reference

                // Upload general billeder
                val generalUrls = mutableListOf<String>()
                for (uri in imageUris.value) {
                    val ref = storage.child("requests/$requestId/general/${UUID.randomUUID()}.jpg")
                    ref.putFile(uri).await()
                    generalUrls.add(ref.downloadUrl.await().toString())
                }

                // Upload stepPhotos med labels
                val labeledUrlsMap = mutableMapOf<String, List<String>>()
                _stepPhotos.value.forEach { (stepId, uris) ->
                    val urls = uris.mapNotNull { uri ->
                        try {
                            val ref = storage.child("requests/$requestId/$stepId/${UUID.randomUUID()}.jpg")
                            ref.putFile(uri).await()
                            ref.downloadUrl.await().toString()
                        } catch (e: Exception) {
                            Timber.e(e, "Step photo upload fejl")
                            null
                        }
                    }
                    if (urls.isNotEmpty()) {
                        val label = when (stepId) {
                            "damage" -> "Billeder af skader"
                            "access" -> "Billeder af adgangsforhold"
                            "openings" -> "Billeder af åbninger"
                            else -> "Billeder fra $stepId"
                        }
                        labeledUrlsMap[label] = urls
                    }
                }

                // Beregn netto areal igen til Request
                val wallArea = d.wallMeasurements.sumOf { ((it.length ?: 0f) * (it.height ?: 0f)).toDouble() }.toFloat()
                val openingArea = d.openingMeasurements.sumOf { ((it.widthCm ?: 0f) / 100f * (it.heightCm ?: 0f) / 100f).toDouble() }.toFloat()
                val nettoArea = (wallArea - openingArea).coerceAtLeast(0f)

                // Byg detailsMap – behold al din tidligere logik (alle felter)
                val detailsMap = mutableMapOf<String, Any>()
                d.murType?.let { detailsMap["murType"] = it }
                d.customMurType?.let { detailsMap["customMurType"] = it }
                d.isRepair?.let { detailsMap["isRepair"] = it }
                d.bearingWall?.let { detailsMap["bearingWall"] = it }
                detailsMap["wallAreaM2"] = wallArea
                detailsMap["openingAreaM2"] = openingArea
                detailsMap["nettoAreaM2"] = nettoArea
                d.thicknessOption?.let { detailsMap["thicknessOption"] = it }
                d.customThickness?.let { detailsMap["customThickness"] = it }
                d.stoneType?.let { detailsMap["stoneType"] = it }
                d.specialStoneName?.let { detailsMap["specialStoneName"] = it }
                d.specialStoneLink?.let { detailsMap["specialStoneLink"] = it }
                d.mortarType?.let { detailsMap["mortarType"] = it }
                d.customMortarType?.let { detailsMap["customMortarType"] = it }
                d.surfaceFinish?.let { detailsMap["surfaceFinish"] = it }
                d.customSurface?.let { detailsMap["customSurface"] = it }
                d.reinforcement?.let { detailsMap["reinforcement"] = it }
                d.insulationWanted?.let { detailsMap["insulationWanted"] = it }
                d.insulationThickness?.let { detailsMap["insulationThickness"] = it }
                d.foundationOption?.let { detailsMap["foundationOption"] = it }
                d.customFoundation?.let { detailsMap["customFoundation"] = it }
                d.hasCracks?.let { detailsMap["hasCracks"] = it }
                d.cracksDescription?.let { detailsMap["cracksDescription"] = it }
                d.hasMoistureDamage?.let { detailsMap["hasMoistureDamage"] = it }
                d.moistureDescription?.let { detailsMap["moistureDescription"] = it }
                d.hasSettlementDamage?.let { detailsMap["hasSettlementDamage"] = it }
                d.settlementDescription?.let { detailsMap["settlementDescription"] = it }
                d.goodAccess?.let { detailsMap["goodAccess"] = it }
                d.accessProblems.let { if (it.isNotEmpty()) detailsMap["accessProblems"] = it }
                d.accessCustomDescription?.let { detailsMap["accessCustomDescription"] = it }
                d.vejrTidspunkt?.let { detailsMap["vejrTidspunkt"] = it }

                val request = Request(
                    id = requestId,
                    userId = currentUser.uid,
                    category = "opmuring",
                    areaM2 = nettoArea,
                    roomType = d.murType ?: "Opmuring",
                    description = descText.ifBlank { null },
                    aiPrice = aiPriceEstimate.value?.toFloat() ?: 0f,
                    images = generalUrls,
                    labeledPhotos = labeledUrlsMap,
                    details = detailsMap
                )

                docRef.set(request).await()
                Timber.d("Opmuring-opgave sendt succesfuldt med ID: $requestId")
                onComplete()
            } catch (e: Exception) {
                Timber.e(e, "Kritisk fejl ved afsendelse af opmuring-opgave")
                setError("Kunne ikke sende opgaven – tjek internetforbindelse og prøv igen")
            } finally {
                setIsSending(false)
            }
        }
    }
}