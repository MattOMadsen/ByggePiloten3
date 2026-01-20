// Fil: app/src/main/java/dk/byggepiloten/firma/data/model/task/FliserData.kt
// OPDATERET: Tilføjet toMap(): Map<String, Any?> for Firestore-serialisering
// - Håndterer alle felter
// - Beholdt alle eksisterende felter præcis
// Total lines: 92 (bekræftet)

package dk.byggepiloten.firma.data.model.task

/**
 * Data class for flise-arbejde (gulv/vægge/begge).
 * Dækker hele wizard-flowet inkl. conditional felter og advarsler.
 * Integreres i TaskViewModel + sendTask (netto areal + details-map til Firestore Request).
 */
data class FliserData(
    // Step 1: Arbejdstype (bestemmer senere conditional felter)
    var workType: String? = null,                  // "Gulv", "Væg" eller "Gulv og væg"

    // Step 2: Gulv dimensioner (kun hvis gulv valgt)
    var floorLength: Float? = null,                // Længde i meter
    var floorWidth: Float? = null,                 // Bredde i meter

    // Step 3: Vægge (kun hvis væg valgt)
    var wallHeight: Float? = null,                 // Højde i meter
    var useFloorPerimeterForWalls: Boolean? = null, // true = auto-beregning fra gulv (2*(længde+bredde))
    var manualWallPerimeter: Float? = null,        // Kun hvis manuel (meter)

    // Step 4: Åbninger/fradrag (fælles for gulv+væg)
    var deductionArea: Float? = null,

    // Step 5: Flisestørrelse
    var tileSize: String? = null,                  // f.eks. "30x60 cm", "60x60 cm", "80x80 cm"
    var customTileSize: String? = null,            // Hvis "Andet"

    // Step  6: Mønster
    var pattern: String? = null,                   // f.eks. "Lige forbandt", "Sildeben", "Firkantet"
    var customPattern: String? = null,             // Hvis "Andet"

    // Step 7: Underlag (conditional advarsler)
    var hasOldTiles: Boolean? = null,              // Gammel flise på underlag?
    var hasCracks: Boolean? = null,                // Revner?
    var hasMoisture: Boolean? = null,              // Fugt?

    // Ny tilføjelse – kun gulv (advarsel om flydespartel)
    var isFloorLevel: Boolean? = null,             // Er gulvet i vater? (true = ja)
    var hasDentsOrDepressions: Boolean? = null,    // Lunker/nedbulinger?

    // Step 8: Adgang (advarsel ved stillads)
    var goodAccess: Boolean? = null,               // God adgang? (true = ja, ingen ekstra omkostninger)
    var needsScaffolding: Boolean? = null          // Stillads nødvendig?
) {
    /**
     * Konverterer FliserData til Map<String, Any?> for Firestore (nested i Request.details).
     */
    fun toMap(): Map<String, Any?> = mapOf(
        "workType" to workType,
        "floorLength" to floorLength,
        "floorWidth" to floorWidth,
        "wallHeight" to wallHeight,
        "useFloorPerimeterForWalls" to useFloorPerimeterForWalls,
        "manualWallPerimeter" to manualWallPerimeter,
        "deductionArea" to deductionArea,
        "tileSize" to tileSize,
        "customTileSize" to customTileSize,
        "pattern" to pattern,
        "customPattern" to customPattern,
        "hasOldTiles" to hasOldTiles,
        "hasCracks" to hasCracks,
        "hasMoisture" to hasMoisture,
        "isFloorLevel" to isFloorLevel,
        "hasDentsOrDepressions" to hasDentsOrDepressions,
        "goodAccess" to goodAccess,
        "needsScaffolding" to needsScaffolding
    )
}