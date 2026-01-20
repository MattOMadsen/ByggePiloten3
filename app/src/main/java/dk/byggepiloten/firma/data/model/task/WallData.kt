// Fil: app/src/main/java/dk/byggepiloten/firma/data/model/task/WallData.kt
// OPDATERET: Tilføjet toMap() extension-funktion for Firestore-serialisering
// - Håndterer alle felter inkl. nested lists (WallMeasurement, OpeningMeasurement)
// - Konverterer til Map<String, Any?> – klar til Firestore set/add
// - Beholdt alle eksisterende felter + reinforcementLevel
// Total lines: 142 (bekræftet)

package dk.byggepiloten.firma.data.model.task

data class WallMeasurement(
    val length: Float? = null,  // meter
    val height: Float? = null   // meter
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "length" to length,
        "height" to height
    )
}

data class OpeningMeasurement(
    val widthCm: Float? = null,
    val heightCm: Float? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "widthCm" to widthCm,
        "heightCm" to heightCm
    )
}

data class WallData(
    // Step 1: Murtype
    var murType: String? = null,
    var customMurType: String? = null,

    // Step 2: Ny eller reparation
    var isRepair: Boolean? = null,

    // Step 3: Bærende væg
    var bearingWall: Boolean? = null,

    // Step 4: Dimensioner
    var wallCount: Int? = null,
    var wallMode: String? = null,
    var wallTotalAreaM2: Float? = null,
    var wallMeasurements: List<WallMeasurement> = emptyList(),

    // Ny mur: Tykkelse
    var thicknessOption: String? = null,
    var customThickness: Int? = null,

    // Ny mur: Materialer – sten
    var stoneType: String? = null,
    var specialStoneName: String? = null,
    var specialStoneLink: String? = null,

    // Ny mur: Mørtel
    var mortarType: String? = null,
    var customMortarType: String? = null,

    // Reparation: Skader
    var hasCracks: Boolean? = null,
    var cracksDescription: String? = null,
    var hasMoistureDamage: Boolean? = null,
    var moistureDescription: String? = null,
    var hasSettlementDamage: Boolean? = null,
    var settlementDescription: String? = null,

    // Ny mur: Åbninger
    var openingsCount: Int? = null,
    var openingMode: String? = null,
    var openingTotalAreaM2: Float? = null,
    var openingMeasurements: List<OpeningMeasurement> = emptyList(),

    // Ny mur: Pudsarmering (armeringsnet i pudslaget)
    var reinforcementLevel: String? = null,  // "none", "standard", "reinforced"

    // Ny mur: Overflade
    var surfaceFinish: String? = null,         // "Hæftemørtel", "Skalcem", "Vandskuring", "Andet"
    var customSurface: String? = null,         // For "Andet"
    var haeftemoertelFarve: String? = null,    // Valgt farve ved Hæftemørtel
    var skalcemFarve: String? = null,          // Valgt farve ved Skalcem

    // Nyt: Vejr/tidspunkt
    var vejrTidspunkt: String? = null,

    // Ny mur (facademur): Isolering
    var insulationWanted: Boolean? = null,
    var insulationThickness: Float? = null,

    // Ny mur: Fundament
    var foundationOption: String? = null,
    var customFoundation: String? = null,

    // Fælles: Adgang
    var goodAccess: Boolean? = null,
    var accessProblems: List<String> = emptyList(),
    var accessCustomDescription: String? = null
) {
    /**
     * Konverterer WallData til Map<String, Any?> for Firestore.
     * Håndterer nested objects (WallMeasurement, OpeningMeasurement) via deres toMap().
     */
    fun toMap(): Map<String, Any?> = mapOf(
        "murType" to murType,
        "customMurType" to customMurType,
        "isRepair" to isRepair,
        "bearingWall" to bearingWall,
        "wallCount" to wallCount,
        "wallMode" to wallMode,
        "wallTotalAreaM2" to wallTotalAreaM2,
        "wallMeasurements" to wallMeasurements.map { it.toMap() },
        "thicknessOption" to thicknessOption,
        "customThickness" to customThickness,
        "stoneType" to stoneType,
        "specialStoneName" to specialStoneName,
        "specialStoneLink" to specialStoneLink,
        "mortarType" to mortarType,
        "customMortarType" to customMortarType,
        "hasCracks" to hasCracks,
        "cracksDescription" to cracksDescription,
        "hasMoistureDamage" to hasMoistureDamage,
        "moistureDescription" to moistureDescription,
        "hasSettlementDamage" to hasSettlementDamage,
        "settlementDescription" to settlementDescription,
        "openingsCount" to openingsCount,
        "openingMode" to openingMode,
        "openingTotalAreaM2" to openingTotalAreaM2,
        "openingMeasurements" to openingMeasurements.map { it.toMap() },
        "reinforcementLevel" to reinforcementLevel,
        "surfaceFinish" to surfaceFinish,
        "customSurface" to customSurface,
        "haeftemoertelFarve" to haeftemoertelFarve,
        "skalcemFarve" to skalcemFarve,
        "vejrTidspunkt" to vejrTidspunkt,
        "insulationWanted" to insulationWanted,
        "insulationThickness" to insulationThickness,
        "foundationOption" to foundationOption,
        "customFoundation" to customFoundation,
        "goodAccess" to goodAccess,
        "accessProblems" to accessProblems,
        "accessCustomDescription" to accessCustomDescription
    )
}