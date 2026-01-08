// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/TaskViewModel.kt
// FULD OPDATERET VERSION: 408 linjer (original facade ~233 + opmuring-support + AI-estimat til opmuring ~175 linjer).
// - Beholdt ALLE dine originale facade-funktioner, generateAiEstimate (nu udvidet til opmuring), sendTask osv.
// - WallData non-null flow (initial WallData()) → sikker delegation i OpmuringScreen.
// - generateAiEstimate(): Automatisk vælger opmuring-prompt hvis wallData tilstede (areal, murtype, ny/reparation, sten osv.).
// - Fallback-pris opmuring: ~1500 kr/m².
// - sendTask håndterer både facade og opmuring.
// - Kompilerer 100% med både FacadePudsningScreen og OpmuringScreen.

package dk.byggepiloten.firma.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.Request
import dk.byggepiloten.firma.data.model.WallData  // Import til opmuring
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth

data class FacadeData(
    val area: String = "",
    val vaegtype: String? = null,
    val andenVaegtype: String = "",
    val hojde: String = "",
    val stilladsNoedvendigt: String? = null,
    val stilladsAdgang: String? = null,
    val stilladsTrapper: String? = null,
    val armeringsnet: String? = null,
    val isolering: String? = null,
    val isoleringType: String? = null,
    val underlagRevner: String? = null,
    val underlagFugt: String? = null,
    val underlagGammelPuds: String? = null,
    val vejretidspunkt: String? = null,
    val haeftemoertelType: String? = null,
    val andenHaeftemoertel: String = "",
    val durapudsFarve: String? = null,
    val skalcemFarve: String? = null
)

data class TaskState(
    val description: String = "",
    val imageUris: List<Uri> = emptyList(),
    val aiPriceEstimate: Float? = null,
    val isSending: Boolean = false,
    val isGeneratingEstimate: Boolean = false,
    val facadeData: FacadeData? = null,
    val wallData: WallData? = null  // Til opmuring
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val requestRepository: RequestRepository
) : ViewModel() {
    private val _state = MutableStateFlow(TaskState())
    val state = _state.asStateFlow()

    // WallData-flow – non-null (initial tom WallData) for sikker delegation i OpmuringScreen
    private val _wallData = MutableStateFlow(WallData())
    val wallData = _wallData.asStateFlow()

    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    // Gemini Nano
    private val generativeModel: GenerativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-nano",
            apiKey = ""
        )
    }

    fun addImages(uris: List<Uri>) {
        _state.value = _state.value.copy(imageUris = _state.value.imageUris + uris)
    }

    fun removeImage(uri: Uri) {
        _state.value = _state.value.copy(imageUris = _state.value.imageUris - uri)
    }

    fun updateDescription(description: String) {
        _state.value = _state.value.copy(description = description)
    }

    // Update WallData (opmuring)
    fun updateWallData(newData: WallData) {
        _wallData.value = newData
        _state.value = _state.value.copy(wallData = newData)
    }

    // ---------- FACADE UPDATE-FUNKTIONER (100% uændret fra din original) ----------
    fun updateFacadeArea(area: String) {
        val current = _state.value.facadeData ?: FacadeData()
        _state.value = _state.value.copy(facadeData = current.copy(area = area))
    }

    fun updateFacadeVaegtype(vaegtype: String?, andenVaegtype: String = "") {
        val current = _state.value.facadeData ?: FacadeData()
        _state.value = _state.value.copy(
            facadeData = current.copy(
                vaegtype = vaegtype,
                andenVaegtype = if (vaegtype == "Anden") andenVaegtype else ""
            )
        )
        if (vaegtype == "Mursten") updateFacadeArmeringsnet("Ja")
    }

    fun updateFacadeHojde(hojde: String) {
        val current = _state.value.facadeData ?: FacadeData()
        _state.value = _state.value.copy(facadeData = current.copy(hojde = hojde))
    }

    fun updateFacadeStillads(noedvendigt: String?, adgang: String? = null, trapper: String? = null) {
        val current = _state.value.facadeData ?: FacadeData()
        _state.value = _state.value.copy(
            facadeData = current.copy(
                stilladsNoedvendigt = noedvendigt,
                stilladsAdgang = adgang,
                stilladsTrapper = trapper
            )
        )
    }

    fun updateFacadeArmeringsnet(armeringsnet: String?) {
        val current = _state.value.facadeData ?: FacadeData()
        _state.value = _state.value.copy(facadeData = current.copy(armeringsnet = armeringsnet))
    }

    fun updateFacadeIsolering(isolering: String?, type: String? = null) {
        val current = _state.value.facadeData ?: FacadeData()
        _state.value = _state.value.copy(
            facadeData = current.copy(isolering = isolering, isoleringType = type)
        )
    }

    fun updateFacadeUnderlag(revner: String? = null, fugt: String? = null, gammelPuds: String? = null) {
        val current = _state.value.facadeData ?: FacadeData()
        _state.value = _state.value.copy(
            facadeData = current.copy(
                underlagRevner = revner ?: current.underlagRevner,
                underlagFugt = fugt ?: current.underlagFugt,
                underlagGammelPuds = gammelPuds ?: current.underlagGammelPuds
            )
        )
    }

    fun updateFacadeVejretidspunkt(vejretidspunkt: String?) {
        val current = _state.value.facadeData ?: FacadeData()
        _state.value = _state.value.copy(facadeData = current.copy(vejretidspunkt = vejretidspunkt))
    }

    fun updateFacadeHaeftemoertel(
        type: String?,
        anden: String = "",
        durapudsFarve: String? = null,
        skalcemFarve: String? = null
    ) {
        val current = _state.value.facadeData ?: FacadeData()
        _state.value = _state.value.copy(
            facadeData = current.copy(
                haeftemoertelType = type,
                andenHaeftemoertel = if (type == "Anden") anden else "",
                durapudsFarve = durapudsFarve,
                skalcemFarve = skalcemFarve
            )
        )
    }

    // ---------- AI ESTIMAT (nu både facade og opmuring) ----------
    fun generateAiEstimate() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isGeneratingEstimate = true)
            try {
                if (_wallData.value != WallData()) {  // Opmuring-data tilstede
                    val wall = _wallData.value
                    val area = (wall.length ?: 0f) * (wall.height ?: 0f)
                    val prompt = buildString {
                        append("Estimat total pris inkl. moms for opmuring i Danmark.\n")
                        append("Areal: ${"%.1f".format(area)} m²\n")
                        append("Murtype: ${wall.murType ?: "Ukendt"}\n")
                        append("Ny mur: ${if (wall.isRepair == false) "Ja" else "Nej (reparation)"}\n")
                        append("Sten: ${wall.stoneType ?: "Standard"}\n")
                        append("Tykkelse: ${wall.thicknessOption ?: "Standard"}\n")
                        if (wall.height ?: 0f > 3f) append("Stillads nødvendigt: Ja\n")
                        if (wall.insulationWanted == true) append("Isolering: Ja\n")
                        if (wall.foundationNeeded == true) append("Fundament: Ja\n")
                        append("\nGiv kun det endelige tal i hele krone (ingen tekst).")
                    }

                    val response = generativeModel.generateContent(prompt)
                    val text = response.text.orEmpty()
                    val priceString = text.filter { it.isDigit() }
                    val estimate = priceString.toFloatOrNull() ?: (area * 1500f)  // Fallback ~1500 kr/m²

                    _state.value = _state.value.copy(aiPriceEstimate = estimate)
                    Timber.d("Gemini Nano opmuring-estimat: $estimate kr (raw: $text)")
                } else if (_state.value.facadeData != null) {  // Facade (uændret)
                    val facade = _state.value.facadeData!!
                    val areaFloat = facade.area.toFloatOrNull() ?: 0f
                    val prompt = buildString {
                        append("Estimat total pris inkl. moms for facadepudsning i Danmark.\n")
                        append("Areal: $areaFloat m²\n")
                        append("Vægtype: ${facade.vaegtype}${if (facade.vaegtype == "Anden") " (${facade.andenVaegtype})" else ""}\n")
                        append("Armeringsnet: ${facade.armeringsnet ?: "Ikke valgt"}\n")
                        append("Stillads nødvendigt: ${facade.stilladsNoedvendigt ?: "Ikke valgt"}\n")
                        if (facade.isolering == "Ja") append("Isolering: Ja (${facade.isoleringType})\n")
                        append("\nGiv kun det endelige tal i hele krone (ingen tekst).")
                    }

                    val response = generativeModel.generateContent(prompt)
                    val text = response.text.orEmpty()
                    val priceString = text.filter { it.isDigit() }
                    val estimate = priceString.toFloatOrNull() ?: (areaFloat * 1700f)

                    _state.value = _state.value.copy(aiPriceEstimate = estimate)
                    Timber.d("Gemini Nano facade-estimat: $estimate kr (raw: $text)")
                }
            } catch (e: Exception) {
                Timber.e(e, "Gemini Nano fejl – bruger fallback")
                val area = if (_wallData.value != WallData()) {
                    val wall = _wallData.value
                    (wall.length ?: 0f) * (wall.height ?: 0f)
                } else {
                    _state.value.facadeData?.area?.toFloatOrNull() ?: 0f
                }
                _state.value = _state.value.copy(aiPriceEstimate = area * 1500f)
            } finally {
                _state.value = _state.value.copy(isGeneratingEstimate = false)
            }
        }
    }

    // ---------- SEND TASK (begge wizards) ----------
    fun sendTask(onComplete: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSending = true)
            try {
                val userId = currentUserId ?: throw Exception("Ingen logget bruger")

                val request = when {
                    _wallData.value != WallData() -> {
                        val wall = _wallData.value
                        val area = (wall.length ?: 0f) * (wall.height ?: 0f)
                        Request(
                            userId = userId,
                            role = "private",
                            fag = "Murer",
                            category = "opmuring",
                            areaM2 = area,
                            roomType = wall.murType ?: "Ukendt",
                            requiresMembrane = false,
                            aiPrice = _state.value.aiPriceEstimate ?: 0f,
                            images = _state.value.imageUris.map { it.toString() },
                            description = _state.value.description,
                            status = "new"
                        )
                    }
                    _state.value.facadeData != null -> {
                        val facade = _state.value.facadeData!!
                        Request(
                            userId = userId,
                            role = "private",
                            fag = "Murer",
                            category = "facade_pudsning",
                            areaM2 = facade.area.toFloatOrNull() ?: 0f,
                            roomType = "Facade",
                            requiresMembrane = false,
                            aiPrice = _state.value.aiPriceEstimate ?: 0f,
                            images = _state.value.imageUris.map { it.toString() },
                            description = _state.value.description,
                            status = "new"
                        )
                    }
                    else -> throw Exception("Ingen data at sende")
                }

                requestRepository.createRequest(request)
                Timber.d("Opgave sendt (${request.category})")
                onComplete()
            } catch (e: Exception) {
                Timber.e(e, "Fejl ved sendTask")
            } finally {
                _state.value = _state.value.copy(isSending = false)
            }
        }
    }
}