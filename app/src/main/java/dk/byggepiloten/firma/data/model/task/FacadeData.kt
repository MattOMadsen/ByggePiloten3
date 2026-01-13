package dk.byggepiloten.firma.data.model.task

data class FacadeData(
    var area: Float? = null,                       // Areal i m² (parsed fra TextField)
    var vaegtype: String? = null,                  // f.eks. "Mursten", "Gasbeton", "Anden"
    var andenVaegtype: String? = null,             // Custom tekst hvis "Anden"

    var hojde: Float? = null,                      // Bygningshøjde i meter (til stillads-beregning)

    var stilladsNoedvendigt: String? = null,       // "Ja" / "Nej"
    var stilladsAdgang: String? = null,            // Kun hvis Ja
    var stilladsTrapper: String? = null,           // Kun hvis Ja

    var armeringsnet: String? = null,              // "Ja" / "Nej" (auto-default "Ja" ved Mursten)

    var isolering: String? = null,                 // "Ja" / "Nej"
    var isoleringType: String? = null,             // Kun hvis Ja

    var underlagRevner: String? = null,            // "Ja" / "Nej"
    var underlagFugt: String? = null,              // "Ja" / "Nej"
    var underlagGammelPuds: String? = null,        // "Ja" / "Nej"

    var vejretidspunkt: String? = null,            // f.eks. "Forår/sommer", "Efterår/vinter"

    var haeftemoertelType: String? = null,         // f.eks. "DuraPuds 615", "Skalcem S2000", "Anden"
    var andenHaeftemoertel: String? = null,        // Custom hvis "Anden"
    var durapudsFarve: String? = null,             // Kun ved DuraPuds
    var skalcemFarve: String? = null               // Kun ved Skalcem
)