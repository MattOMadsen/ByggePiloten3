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
    var demolishKlinkerGulv: Boolean? = null, // NY: Klinker på gulv
    var demolishFixtures: Boolean? = null, // Inventar generelt
    var demolishVask: Boolean? = null, // Sub-inventar
    var demolishToilet: Boolean? = null,
    var demolishBrus: Boolean? = null,
    var demolishBrusevaeg: Boolean? = null,
    var demolishPipes: Boolean? = null,
    var disposalNeeded: Boolean? = null,

    // Step 6: Klinker gulv
    var floorTileSize: String? = null,
    var floorTilePattern: String? = null,
    var customFloorPattern: String? = null,

    // Step 7: Fliser vægge
    var wallTileSize: String? = null,
    var customWallPattern: String? = null, // NY: For "Andet" i væg
    var tilesToCeiling: Boolean? = null,
    var wallTileHeightIfNotCeiling: Float? = null,
    var wallManualArea: Float? = null, // NY: Manuel m² hvis perimeter ikke passer

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
    var floorNumber: Int? = null // NY: Etage hvis trappeopgang
)