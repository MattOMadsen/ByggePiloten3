// Fil: app/src/main/java/dk/byggepiloten/firma/data/model/WallData.kt
// OPDATERET: Tilføjet alle manglende felter fra Opmuring-wizard (inkl. nested data classes for multiple vægge/åbninger).
// Nu fuld MVVM-support – alle steps kan bruge data.copy(...) uden local state i screen.
// Nested classes for bedre struktur og immutable lists.

package dk.byggepiloten.firma.data.model.task

data class WallMeasurement(
    val length: Float? = null,  // meter
    val height: Float? = null   // meter
)

data class OpeningMeasurement(
    val widthCm: Float? = null,
    val heightCm: Float? = null
)

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
    var wallMode: String? = null,                 // "samlet" eller "individuel"
    var wallTotalAreaM2: Float? = null,
    var wallMeasurements: List<WallMeasurement> = emptyList(),

    // Ny mur: Tykkelse
    var thicknessOption: String? = null,
    var customThickness: Int? = null,

    // Ny mur: Materialer
    var stoneType: String? = null,
    var customStoneType: String? = null,
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

    // Ny mur: Armering
    var reinforcement: Boolean? = null,

    // Ny mur: Overflade
    var surfaceFinish: String? = null,
    var customSurface: String? = null,

    // Ny mur (kun facademur): Isolering
    var insulationWanted: Boolean? = null,
    var insulationThickness: Float? = null,       // cm

    // Ny mur: Fundament
    var foundationOption: String? = null,
    var customFoundation: String? = null,

    // Fælles: Adgang
    var goodAccess: Boolean? = null
)