// Fil: app/src/main/java/dk/byggepiloten/firma/data/model/task/WallData.kt
// FULD OPDATERET – Tilføjet nye felter til special sten
// specialStoneName: String? (navn)
// specialStoneLink: String? (link – valgfrit)
// Felter placeret efter stoneType for logisk rækkefølge

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
    var wallMode: String? = null,
    var wallTotalAreaM2: Float? = null,
    var wallMeasurements: List<WallMeasurement> = emptyList(),

    // Ny mur: Tykkelse
    var thicknessOption: String? = null,
    var customThickness: Int? = null,

    // Ny mur: Materialer – sten
    var stoneType: String? = null,
    var specialStoneName: String? = null,     // NYT: Navn på special sten
    var specialStoneLink: String? = null,     // NYT: Valgfrit link til special sten

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

    // Ny mur: Armering
    var reinforcement: Boolean? = null,

    // Ny mur: Overflade
    var surfaceFinish: String? = null,
    var customSurface: String? = null,

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
)