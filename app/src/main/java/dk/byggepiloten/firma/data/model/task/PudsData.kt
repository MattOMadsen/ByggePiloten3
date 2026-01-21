// Fil: app/src/main/java/dk/byggepiloten/firma/data/model/task/PudsData.kt
// NY FIL – data-model for Puds-kategorien
// Kopi af FacadeData + indeUde øverst (styrer conditional flow)

package dk.byggepiloten.firma.data.model.task

data class VaegMaaling(
    val bredde: Float? = null,
    val hojde: Float? = null
) {
    val areal: Float? = if (bredde != null && hojde != null) bredde * hojde else null
}

data class PudsData(
    var indeUde: String? = null,                   // "Inde" eller "Ude" – styrer hele flowet

    var vaegMaalinger: List<VaegMaaling> = emptyList(), // Individuelle vægge
    var area: Float? = null,                       // Total areal (beregnet live)

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
)