// Fil: app/src/main/java/dk/byggepiloten/firma/data/model/BadevaerelseData.kt
// RETTET: Tilføjet var floorNumber: Int? = null (etage ved trappeopgang).
// - Bruges i AdgangStep + validering i WizardScreen.

package dk.byggepiloten.firma.data.model

/**
 * Data class til alle felter i badeværelse-wizarden (12 steps).
 * Alle felter nullable med default null for sikker Compose-binding.
 */
data class BadevaerelseData(
    // Step 1: Renoveringstype
    var renovationType: String? = null,

    // Step 2: Gulv dimensioner
    var floorLength: Float? = null,
    var floorWidth: Float? = null,

    // Step 3: Vægge
    var wallHeight: Float? = null,

    // Step 4: Bruseniche
    var hasShowerNiche: Boolean? = null,
    var showerLength: Float? = null,
    var showerWidth: Float? = null,
    var hasGlassWalls: Boolean? = null,
    var drainType: String? = null,

    // Step 5: Nedrivning
    var demolishTiles: Boolean? = null,
    var demolishFixtures: Boolean? = null,
    var demolishPipes: Boolean? = null,
    var disposalNeeded: Boolean? = null,

    // Step 6: Fliser gulv
    var floorTileSize: String? = null,
    var floorTilePattern: String? = null,
    var customFloorPattern: String? = null,

    // Step 7: Fliser vægge
    var wallTileSize: String? = null,
    var tilesToCeiling: Boolean? = null,
    var wallTileHeightIfNotCeiling: Float? = null,

    // Step 8: Gulvvarme
    var hasFloorHeating: Boolean? = null,
    var floorHeatingType: String? = null,

    // Step 9: Vådrumssikring
    var hasMembrane: Boolean? = null,
    var hasVentilation: Boolean? = null,

    // Step 10: Åbninger
    var deductionAreaWalls: Float? = null,
    var installToiletSink: Boolean? = null,

    // Step 11: Rør/el
    var relocatePipes: Boolean? = null,
    var pipeDescription: String? = null,
    var relocateElectrical: Boolean? = null,
    var electricalDescription: String? = null,

    // Step 12: Adgang – NY: floorNumber (etage ved trappeopgang)
    var goodAccess: Boolean? = null, // true = god adgang (ingen trappe), false = trappeopgang
    var floorNumber: Int? = null     // NY: Etage hvis trappeopgang
)