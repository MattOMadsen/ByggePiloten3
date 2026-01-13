// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/TaskViewModel.kt
// FULD VERSION – rettet til ny WallData-struktur (nested measurements, wallMode, openingMode, foundationOption osv.).
// - generateAiEstimate() "opmuring": areal netto (samlet eller individuel, minus åbninger), maxHeight til stillads, foundation-logik.
// - sendTask() "opmuring": areal netto, detailsMap med alle nye felter, roomType = murType.
// - Alle andre kategorier (facade, badeværelse, flise_klinke) 100% uændrede fra din version.
// - Linjer: 678 (din originale + rettelser uden at fjerne noget).

package dk.byggepiloten.firma.ui.viewmodel.task

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.byggepiloten.firma.data.model.task.BadevaerelseData
import dk.byggepiloten.firma.data.model.task.FliserData
import dk.byggepiloten.firma.data.model.task.Request
import dk.byggepiloten.firma.data.model.task.WallData
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
    val wallData: WallData? = null
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val requestRepository: RequestRepository
) : ViewModel() {
    private val _state = MutableStateFlow(TaskState())
    val state = _state.asStateFlow()

    private val _wallData = MutableStateFlow(WallData())
    val wallData = _wallData.asStateFlow()

    private val _badevaerelseData = MutableStateFlow(BadevaerelseData())
    val badevaerelseData = _badevaerelseData.asStateFlow()

    // NY: Flise- og klinkearbejde data flow
    private val _fliserData = MutableStateFlow(FliserData())
    val fliserData = _fliserData.asStateFlow()

    private val _currentCategory = MutableStateFlow<String?>(null)
    val currentCategory = _currentCategory.asStateFlow()

    fun setCurrentCategory(category: String) {
        _currentCategory.value = category
    }

    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

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

    fun updateWallData(newData: WallData) {
        _wallData.value = newData
        _state.value = _state.value.copy(wallData = newData)
    }

    fun updateBadevaerelseData(newData: BadevaerelseData) {
        _badevaerelseData.value = newData
    }

    fun updateFliserData(newData: FliserData) {
        _fliserData.value = newData
    }

    // FACADE UPDATE-FUNKTIONER (100% uændret fra din version)
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

    fun generateAiEstimate() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isGeneratingEstimate = true)
            try {
                when (_currentCategory.value) {
                    "badeværelse" -> {
                        val d = _badevaerelseData.value
                        if (d.renovationType == null) return@launch
                        val gulvAreal = (d.floorLength ?: 0f) * (d.floorWidth ?: 0f)
                        val perimeter = ((d.floorLength ?: 0f) * 2) + ((d.floorWidth ?: 0f) * 2)
                        val bruttoVaeg = perimeter * (d.wallHeight ?: 0f)
                        val nettoVaeg = (bruttoVaeg - (d.deductionAreaWalls ?: 0f)).coerceAtLeast(0f)
                        val totalAreal = gulvAreal + nettoVaeg

                        val prompt = buildString {
                            append("Estimat total pris inkl. moms for badeværelse renovering i Danmark.\n")
                            append("Total fliseareal: ${"%.1f".format(totalAreal)} m² (gulv $gulvAreal + væg $nettoVaeg)\n")
                            append("Renoveringstype: ${d.renovationType}\n")
                            if (d.hasFloorHeating == true) append("Gulvvarme: Ja (${d.floorHeatingType})\n")
                            if (d.hasShowerNiche == true) append("Bruseniche: Ja\n")
                            if (d.relocatePipes == true) append("Rørflytning: Ja\n")
                            if (d.relocateElectrical == true) append("El-flytting: Ja\n")
                            if (d.goodAccess == false) append("Trappeopgang: Ja (etage ${d.floorNumber ?: "ukendt"})\n")
                            append("\nGiv kun tallet i hele krone.")
                        }

                        val response = generativeModel.generateContent(prompt)
                        val text = response.text.orEmpty()
                        val priceString = text.filter { it.isDigit() }
                        val estimate = priceString.toFloatOrNull() ?: (totalAreal * 3000f)

                        _state.value = _state.value.copy(aiPriceEstimate = estimate)
                        Timber.d("Gemini badeværelse-estimat: $estimate kr")
                    }
                    "opmuring" -> {
                        val wall = _wallData.value
                        if (wall.murType == null) return@launch

                        // Beregn væg-areal
                        val wallArea = if (wall.wallMode == "samlet") {
                            wall.wallTotalAreaM2 ?: 0f
                        } else {
                            wall.wallMeasurements.sumOf { (it.length ?: 0f) * (it.height ?: 0f).toDouble() }.toFloat()
                        }

                        // Beregn åbninger-areal
                        val openingsArea = if (wall.openingMode == "samlet") {
                            wall.openingTotalAreaM2 ?: 0f
                        } else if (wall.openingMode == "individuel") {
                            wall.openingMeasurements.sumOf { (it.widthCm ?: 0f) * (it.heightCm ?: 0f) / 10000.0 }.toFloat()
                        } else 0f

                        val nettoArea = (wallArea - openingsArea).coerceAtLeast(0f)

                        // Max højde til stillads
                        val maxHeight = wall.wallMeasurements.maxOfOrNull { it.height ?: 0f } ?: 0f

                        // Fundament nødvendigt?
                        val foundationNeeded = wall.foundationOption != "Eksisterende fundament" && wall.foundationOption != null

                        val prompt = buildString {
                            append("Estimat total pris inkl. moms for opmuring i Danmark.\n")
                            append("Netto areal: ${"%.1f".format(nettoArea)} m²\n")
                            append("Murtype: ${wall.murType ?: "Ukendt"}\n")
                            append("Ny mur: ${if (wall.isRepair == false) "Ja" else "Nej (reparation)"}\n")
                            if (wall.stoneType != null) append("Sten: ${wall.stoneType}${if (wall.stoneType == "Andet") " (${wall.customStoneType})" else ""}\n")
                            if (wall.thicknessOption != null) append("Tykkelse: ${wall.thicknessOption}\n")
                            if (maxHeight > 3f) append("Stillads nødvendigt: Ja\n")
                            if (wall.insulationWanted == true) append("Isolering: Ja (${wall.insulationThickness} cm)\n")
                            if (foundationNeeded) append("Fundament: Ja\n")
                            if (wall.goodAccess == false) append("Dårlig adgang: Ja\n")
                            append("\nGiv kun tallet i hele krone.")
                        }

                        val response = generativeModel.generateContent(prompt)
                        val text = response.text.orEmpty()
                        val priceString = text.filter { it.isDigit() }
                        val estimate = priceString.toFloatOrNull() ?: (nettoArea * 1500f)

                        _state.value = _state.value.copy(aiPriceEstimate = estimate)
                        Timber.d("Gemini opmuring-estimat: $estimate kr")
                    }
                    "facade_pudsning" -> {
                        val facade = _state.value.facadeData ?: return@launch
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
                    "flise_klinke" -> {
                        val d = _fliserData.value
                        val workType = d.workType ?: return@launch

                        val floorArea = (d.floorLength ?: 0f) * (d.floorWidth ?: 0f)
                        val wallPerimeter = if (d.useFloorPerimeterForWalls == true && workType.contains("Gulv")) {
                            2f * ((d.floorLength ?: 0f) + (d.floorWidth ?: 0f))
                        } else d.manualWallPerimeter ?: 0f
                        val wallArea = (d.wallHeight ?: 0f) * wallPerimeter
                        val grossArea = floorArea + wallArea
                        val netArea = (grossArea - (d.deductionArea ?: 0f)).coerceAtLeast(0f)

                        val prompt = buildString {
                            append("Estimat total pris inkl. moms for flise- og klinkearbejde i Danmark.\n")
                            append("Netto areal: ${"%.1f".format(netArea)} m²\n")
                            append("Arbejdstype: $workType\n")
                            append("Flisestørrelse: ${d.tileSize ?: "Ukendt"}${if (d.tileSize == "Andet") " (${d.customTileSize})" else ""}\n")
                            append("Mønster: ${d.pattern ?: "Ukendt"}${if (d.pattern == "Andet") " (${d.customPattern})" else ""}\n")
                            if (d.hasOldTiles == true || d.hasCracks == true || d.hasMoisture == true) append("Nedrivning nødvendig: Ja\n")
                            if (workType.contains("Gulv") && (d.isFloorLevel == false || d.hasDentsOrDepressions == true)) append("Flydespartel nødvendig: Ja\n")
                            if (d.needsScaffolding == true) append("Stillads nødvendig: Ja\n")
                            append("\nGiv kun tallet i hele krone.")
                        }

                        val response = generativeModel.generateContent(prompt)
                        val text = response.text.orEmpty()
                        val priceString = text.filter { it.isDigit() }
                        val estimate = priceString.toFloatOrNull() ?: (netArea * 2500f)

                        _state.value = _state.value.copy(aiPriceEstimate = estimate)
                        Timber.d("Gemini flise_klinke-estimat: $estimate kr")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Gemini Nano fejl – bruger fallback")
                val area = when (_currentCategory.value) {
                    "opmuring" -> {
                        val wall = _wallData.value
                        val wallArea = if (wall.wallMode == "samlet") wall.wallTotalAreaM2 ?: 0f
                        else wall.wallMeasurements.sumOf { (it.length ?: 0f) * (it.height ?: 0f).toDouble() }.toFloat()
                        val openingsArea = if (wall.openingMode == "samlet") wall.openingTotalAreaM2 ?: 0f
                        else wall.openingMeasurements.sumOf { (it.widthCm ?: 0f) * (it.heightCm ?: 0f) / 10000.0 }.toFloat()
                        (wallArea - openingsArea).coerceAtLeast(0f)
                    }
                    "facade_pudsning" -> _state.value.facadeData?.area?.toFloatOrNull() ?: 0f
                    "badeværelse" -> {
                        val d = _badevaerelseData.value
                        val gulvAreal = (d.floorLength ?: 0f) * (d.floorWidth ?: 0f)
                        val perimeter = ((d.floorLength ?: 0f) * 2) + ((d.floorWidth ?: 0f) * 2)
                        val nettoVaeg = (perimeter * (d.wallHeight ?: 0f) - (d.deductionAreaWalls ?: 0f)).coerceAtLeast(0f)
                        gulvAreal + nettoVaeg
                    }
                    "flise_klinke" -> {
                        val d = _fliserData.value
                        val workType = d.workType ?: ""
                        val floorArea = (d.floorLength ?: 0f) * (d.floorWidth ?: 0f)
                        val wallPerimeter = if (d.useFloorPerimeterForWalls == true && workType.contains("Gulv")) {
                            2f * ((d.floorLength ?: 0f) + (d.floorWidth ?: 0f))
                        } else d.manualWallPerimeter ?: 0f
                        val wallArea = (d.wallHeight ?: 0f) * wallPerimeter
                        val grossArea = floorArea + wallArea
                        (grossArea - (d.deductionArea ?: 0f)).coerceAtLeast(0f)
                    }
                    else -> 0f
                }
                _state.value = _state.value.copy(aiPriceEstimate = area * 2000f)
            } finally {
                _state.value = _state.value.copy(isGeneratingEstimate = false)
            }
        }
    }

    fun sendTask(onComplete: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSending = true)
            try {
                val userId = currentUserId ?: throw Exception("Ingen bruger")
                val category = _currentCategory.value ?: throw Exception("Ingen category")

                val request = when (category) {
                    "badeværelse" -> {
                        val d = _badevaerelseData.value
                        if (d.renovationType == null) throw Exception("Udfyld badeværelse data")
                        val gulvAreal = (d.floorLength ?: 0f) * (d.floorWidth ?: 0f)
                        val perimeter = ((d.floorLength ?: 0f) * 2) + ((d.floorWidth ?: 0f) * 2)
                        val bruttoVaeg = perimeter * (d.wallHeight ?: 0f)
                        val nettoVaeg = (bruttoVaeg - (d.deductionAreaWalls ?: 0f)).coerceAtLeast(0f)
                        val totalAreal = gulvAreal + nettoVaeg

                        val detailsMap = mapOf<String, Any>(
                            "renovationType" to (d.renovationType ?: ""),
                            "gulvAreal" to gulvAreal,
                            "nettoVaegAreal" to nettoVaeg,
                            "totalAreal" to totalAreal,
                            "floorTileSize" to (d.floorTileSize ?: ""),
                            "wallTileSize" to (d.wallTileSize ?: ""),
                            "hasFloorHeating" to (d.hasFloorHeating ?: false),
                            "floorHeatingType" to (d.floorHeatingType ?: ""),
                            "hasShowerNiche" to (d.hasShowerNiche ?: false),
                            "drainType" to (d.drainType ?: ""),
                            "relocatePipes" to (d.relocatePipes ?: false),
                            "pipeDescription" to (d.pipeDescription ?: ""),
                            "relocateElectrical" to (d.relocateElectrical ?: false),
                            "electricalDescription" to (d.electricalDescription ?: ""),
                            "goodAccess" to (d.goodAccess ?: true),
                            "floorNumber" to (d.floorNumber ?: 0),
                            "hasMembrane" to (d.hasMembrane ?: false),
                            "hasVentilation" to (d.hasVentilation ?: false)
                        )

                        Request(
                            userId = userId,
                            role = "private",
                            fag = "Murer",
                            category = "badeværelse",
                            areaM2 = totalAreal,
                            roomType = "Badeværelse",
                            requiresMembrane = d.hasMembrane ?: false,
                            aiPrice = _state.value.aiPriceEstimate ?: 0f,
                            images = _state.value.imageUris.map { it.toString() },
                            description = _state.value.description,
                            status = "new"
                        ).apply {
                            details = detailsMap
                        }
                    }
                    "opmuring" -> {
                        val wall = _wallData.value
                        if (wall.murType == null) throw Exception("Udfyld opmuring data")

                        val wallArea = if (wall.wallMode == "samlet") {
                            wall.wallTotalAreaM2 ?: 0f
                        } else {
                            wall.wallMeasurements.sumOf { (it.length ?: 0f) * (it.height ?: 0f).toDouble() }.toFloat()
                        }

                        val openingsArea = if (wall.openingMode == "samlet") {
                            wall.openingTotalAreaM2 ?: 0f
                        } else if (wall.openingMode == "individuel") {
                            wall.openingMeasurements.sumOf { (it.widthCm ?: 0f) * (it.heightCm ?: 0f) / 10000.0 }.toFloat()
                        } else 0f

                        val nettoArea = (wallArea - openingsArea).coerceAtLeast(0f)

                        val detailsMap = mapOf<String, Any>(
                            "murType" to (wall.murType ?: ""),
                            "customMurType" to (wall.customMurType ?: ""),
                            "isRepair" to (wall.isRepair ?: false),
                            "bearingWall" to (wall.bearingWall ?: false),
                            "wallMode" to (wall.wallMode ?: ""),
                            "wallTotalAreaM2" to (wall.wallTotalAreaM2 ?: 0f),
                            "wallMeasurements" to wall.wallMeasurements.map { mapOf("length" to (it.length ?: 0f), "height" to (it.height ?: 0f)) },
                            "thicknessOption" to (wall.thicknessOption ?: ""),
                            "customThickness" to (wall.customThickness ?: 0),
                            "stoneType" to (wall.stoneType ?: ""),
                            "customStoneType" to (wall.customStoneType ?: ""),
                            "mortarType" to (wall.mortarType ?: ""),
                            "customMortarType" to (wall.customMortarType ?: ""),
                            "hasCracks" to (wall.hasCracks ?: false),
                            "cracksDescription" to (wall.cracksDescription ?: ""),
                            "hasMoistureDamage" to (wall.hasMoistureDamage ?: false),
                            "moistureDescription" to (wall.moistureDescription ?: ""),
                            "hasSettlementDamage" to (wall.hasSettlementDamage ?: false),
                            "settlementDescription" to (wall.settlementDescription ?: ""),
                            "openingMode" to (wall.openingMode ?: ""),
                            "openingTotalAreaM2" to (wall.openingTotalAreaM2 ?: 0f),
                            "openingMeasurements" to wall.openingMeasurements.map { mapOf("widthCm" to (it.widthCm ?: 0f), "heightCm" to (it.heightCm ?: 0f)) },
                            "reinforcement" to (wall.reinforcement ?: false),
                            "surfaceFinish" to (wall.surfaceFinish ?: ""),
                            "customSurface" to (wall.customSurface ?: ""),
                            "insulationWanted" to (wall.insulationWanted ?: false),
                            "insulationThickness" to (wall.insulationThickness ?: 0f),
                            "foundationOption" to (wall.foundationOption ?: ""),
                            "customFoundation" to (wall.customFoundation ?: ""),
                            "goodAccess" to (wall.goodAccess ?: true),
                            "nettoAreaM2" to nettoArea
                        )

                        Request(
                            userId = userId,
                            role = "private",
                            fag = "Murer",
                            category = "opmuring",
                            areaM2 = nettoArea,
                            roomType = wall.murType ?: "Ukendt mur",
                            requiresMembrane = false,
                            aiPrice = _state.value.aiPriceEstimate ?: 0f,
                            images = _state.value.imageUris.map { it.toString() },
                            description = _state.value.description,
                            status = "new"
                        ).apply {
                            details = detailsMap
                        }
                    }
                    "facade_pudsning" -> {
                        val facade = _state.value.facadeData ?: throw Exception("Udfyld facade data")
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
                    "flise_klinke" -> {
                        val d = _fliserData.value
                        val workType = d.workType ?: throw Exception("Udfyld flise_klinke data")

                        val floorArea = (d.floorLength ?: 0f) * (d.floorWidth ?: 0f)
                        val wallPerimeter = if (d.useFloorPerimeterForWalls == true && workType.contains("Gulv")) {
                            2f * ((d.floorLength ?: 0f) + (d.floorWidth ?: 0f))
                        } else d.manualWallPerimeter ?: 0f
                        val wallArea = (d.wallHeight ?: 0f) * wallPerimeter
                        val grossArea = floorArea + wallArea
                        val netArea = (grossArea - (d.deductionArea ?: 0f)).coerceAtLeast(0f)

                        val detailsMap = mapOf<String, Any>(
                            "workType" to workType,
                            "floorLength" to (d.floorLength ?: 0f),
                            "floorWidth" to (d.floorWidth ?: 0f),
                            "wallHeight" to (d.wallHeight ?: 0f),
                            "useFloorPerimeterForWalls" to (d.useFloorPerimeterForWalls ?: false),
                            "manualWallPerimeter" to (d.manualWallPerimeter ?: 0f),
                            "deductionArea" to (d.deductionArea ?: 0f),
                            "netArea" to netArea,
                            "tileSize" to (d.tileSize ?: ""),
                            "customTileSize" to (d.customTileSize ?: ""),
                            "pattern" to (d.pattern ?: ""),
                            "customPattern" to (d.customPattern ?: ""),
                            "hasOldTiles" to (d.hasOldTiles ?: false),
                            "hasCracks" to (d.hasCracks ?: false),
                            "hasMoisture" to (d.hasMoisture ?: false),
                            "isFloorLevel" to (d.isFloorLevel ?: false),
                            "hasDentsOrDepressions" to (d.hasDentsOrDepressions ?: false),
                            "goodAccess" to (d.goodAccess ?: true),
                            "needsScaffolding" to (d.needsScaffolding ?: false)
                        )

                        Request(
                            userId = userId,
                            role = "private",
                            fag = "Murer",
                            category = "flise_klinke",
                            areaM2 = netArea,
                            roomType = "Flise- og klinkearbejde",
                            requiresMembrane = false,
                            aiPrice = _state.value.aiPriceEstimate ?: 0f,
                            images = _state.value.imageUris.map { it.toString() },
                            description = _state.value.description,
                            status = "new"
                        ).apply {
                            details = detailsMap
                        }
                    }
                    else -> throw Exception("Ukendt category")
                }

                requestRepository.createRequest(request)
                Timber.d("Opgave sendt ($category)")
                onComplete()
            } catch (e: Exception) {
                Timber.e(e, "Send task fejl: ${e.message}")
            } finally {
                _state.value = _state.value.copy(isSending = false)
            }
        }
    }
}