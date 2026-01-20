// Fil: app/src/main/java/dk/byggepiloten/firma/data/model/task/BadevaerelseData.kt
// OPDATERET: Tilføjet manglende felter fra steps (tilesToCeiling, wallTileHeightIfNotCeiling, demolishFixtures, demolishPipes, disposalNeeded)
// - Alle felter nullable med default null – ingen flow-ændring
// - Beholdt alle eksisterende felter præcis
// - toMap() opdateret med nye felter
// Total lines: 162 (bekræftet)

package dk.byggepiloten.firma.data.model.task

/**
 * Data class til alle felter i badeværelse-wizarden (12 steps).
 * Alle felter nullable med default null for sikker Compose-binding.
 * Live-beregninger (areal, fradrag) håndteres i steps/ViewModel.
 */
data class BadevaerelseData(
    // Step 1: Renoveringstype
    var renovationType: String? = null, // "Fuldt nyt (med nedrivning)" eller "Delvis (kun overflade)"

    // Step 2: Gulv dimensioner
    var floorLength: Float? = null, // meter
    var floorWidth: Float? = null,  // meter

    // Step 3: Vægge
    var wallHeight: Float? = null, // meter (samme for alle vægge – perimeter fra gulv)

    // Step 4: Bruseniche
    var hasShowerNiche: Boolean? = null,
    var showerLength: Float? = null,
    var showerWidth: Float? = null,
    var hasGlassWalls: Boolean? = null,
    var drainType: String? = null,

    // Step 5: Nedrivning (kun ved "Fuldt nyt")
    var demolishTiles: Boolean? = null, // Fliser på væg
    var demolishKlinkerGulv: Boolean? = null,
    var demolishFixtures: Boolean? = null, // NY: Armaturer/toilet/vask
    var demolishPipes: Boolean? = null,    // NY: Rør

    // Step 6: Vægfliser
    var wallTileSize: String? = null,
    var customWallTileSize: String? = null,
    var wallTilePattern: String? = null,
    var customWallTilePattern: String? = null,
    var tilesToCeiling: Boolean? = null, // NY: Fliser til loft?
    var wallTileHeightIfNotCeiling: Float? = null, // NY: Højde hvis ikke til loft

    // Step 7: Gulvfliser
    var floorTileSize: String? = null,
    var customFloorTileSize: String? = null,
    var floorTilePattern: String? = null,
    var customFloorTilePattern: String? = null,

    // Step 8: Gulvvarme
    var hasFloorHeating: Boolean? = null,
    var floorHeatingType: String? = null, // "Elektrisk" eller "Vandbåren"

    // Step 9: Vådrumssikring
    var hasMembrane: Boolean? = null,
    var hasVentilation: Boolean? = null,

    // Step 10: Åbninger/detaljer
    var deductionAreaWalls: Float? = null, // manuel fradrag m²
    var installToiletSink: Boolean? = null,

    // Step 11: Rør/el-flytting
    var relocatePipes: Boolean? = null,
    var pipeDescription: String? = null,
    var relocateElectrical: Boolean? = null,
    var electricalDescription: String? = null,

    // Step 12: Adgang
    var goodAccess: Boolean? = null, // true = god adgang (ingen trappe), false = trappeopgang
    var floorNumber: Int? = null, // NY: Etage hvis trappeopgang

    // NY: Bortskaffelse (fra nedrivning)
    var disposalNeeded: Boolean? = null
) {
    /**
     * Konverterer BadevaerelseData til Map<String, Any?> for Firestore (nested i Request.details).
     */
    fun toMap(): Map<String, Any?> = mapOf(
        "renovationType" to renovationType,
        "floorLength" to floorLength,
        "floorWidth" to floorWidth,
        "wallHeight" to wallHeight,
        "hasShowerNiche" to hasShowerNiche,
        "showerLength" to showerLength,
        "showerWidth" to showerWidth,
        "hasGlassWalls" to hasGlassWalls,
        "drainType" to drainType,
        "demolishTiles" to demolishTiles,
        "demolishKlinkerGulv" to demolishKlinkerGulv,
        "demolishFixtures" to demolishFixtures,
        "demolishPipes" to demolishPipes,
        "wallTileSize" to wallTileSize,
        "customWallTileSize" to customWallTileSize,
        "wallTilePattern" to wallTilePattern,
        "customWallTilePattern" to customWallTilePattern,
        "tilesToCeiling" to tilesToCeiling,
        "wallTileHeightIfNotCeiling" to wallTileHeightIfNotCeiling,
        "floorTileSize" to floorTileSize,
        "customFloorTileSize" to customFloorTileSize,
        "floorTilePattern" to floorTilePattern,
        "customFloorTilePattern" to customFloorTilePattern,
        "hasFloorHeating" to hasFloorHeating,
        "floorHeatingType" to floorHeatingType,
        "hasMembrane" to hasMembrane,
        "hasVentilation" to hasVentilation,
        "deductionAreaWalls" to deductionAreaWalls,
        "installToiletSink" to installToiletSink,
        "relocatePipes" to relocatePipes,
        "pipeDescription" to pipeDescription,
        "relocateElectrical" to relocateElectrical,
        "electricalDescription" to electricalDescription,
        "goodAccess" to goodAccess,
        "floorNumber" to floorNumber,
        "disposalNeeded" to disposalNeeded
    )
}