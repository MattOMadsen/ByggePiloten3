// Fil: app/src/main/java/dk/byggepiloten/firma/data/model/task/PudsData.kt
// OPDATERET – genbruger WallMeasurement fra opmuring (length/height i stedet for bredde/hojde)
// - Fjerner VaegMaaling (unødvendig duplikat)
// - area beholdt som total (live-beregnet eller input)
// - toMap() opdateret til at håndtere WallMeasurement
// - 100% konsistent med WallData.kt – maksimal genbrug

package dk.byggepiloten.firma.data.model.task

// Genbruger WallMeasurement fra opmuring (identisk struktur)
import dk.byggepiloten.firma.data.model.task.WallMeasurement

/**
 * Fuldt data-model for Puds-kategorien.
 * Indeholder alle felter fra wizard-trinene inkl. conditional felter baseret på inde/ude.
 * Bruges af PudsTaskViewModel og gemmes til Firestore via toMap().
 * Genbruger WallMeasurement for maksimal konsistens med opmuring.
 */
data class PudsData(
    var indeUde: String? = null,                   // "Inde" eller "Ude" – styrer hele flowet

    var wallMeasurements: List<WallMeasurement> = emptyList(), // Individuelle vægge (genbrug fra opmuring)
    var area: Float? = null,                       // Total areal (beregnet live eller input)

    var vaegtype: String? = null,
    var andenVaegtype: String? = null,

    var hojde: Float? = null,

    var stilladsNoedvendigt: String? = null,
    var stilladsAdgang: String? = null,
    var stilladsTrapper: String? = null,

    var underlagRevner: String? = null,
    var underlagFugt: String? = null,
    var underlagGammelPuds: String? = null,

    var vejretidspunkt: String? = null,

    var armeringsnet: String? = null,

    var isolering: String? = null,
    var isoleringType: String? = null,

    var haeftemoertelType: String? = null,
    var andenHaeftemoertel: String? = null,
    var durapudsFarve: String? = null,
    var skalcemFarve: String? = null
) {
    /**
     * Konverterer hele PudsData til Map<String, Any?> for Firestore.
     * Håndterer nested WallMeasurement via dens toMap().
     */
    fun toMap(): Map<String, Any?> = mapOf(
        "indeUde" to indeUde,

        "wallMeasurements" to wallMeasurements.map { it.toMap() },
        "area" to area,

        "vaegtype" to vaegtype,
        "andenVaegtype" to andenVaegtype,

        "hojde" to hojde,

        "stilladsNoedvendigt" to stilladsNoedvendigt,
        "stilladsAdgang" to stilladsAdgang,
        "stilladsTrapper" to stilladsTrapper,

        "underlagRevner" to underlagRevner,
        "underlagFugt" to underlagFugt,
        "underlagGammelPuds" to underlagGammelPuds,

        "vejretidspunkt" to vejretidspunkt,

        "armeringsnet" to armeringsnet,

        "isolering" to isolering,
        "isoleringType" to isoleringType,

        "haeftemoertelType" to haeftemoertelType,
        "andenHaeftemoertel" to andenHaeftemoertel,
        "durapudsFarve" to durapudsFarve,
        "skalcemFarve" to skalcemFarve
    )
}