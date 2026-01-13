// File: app/src/main/java/dk/byggepiloten/firma/data/model/PriceCategories.kt
// NY FIL – oprettet for at flytte priskategorier og subpriser ud af FirmaPriceSetupScreen.kt for at undgå lang kode.
// Trin-for-trin forklaring:
// 1. Definer en sealed class PriceCategory med underklasser for hver kategori (Netpuds, Omfugning, Badeværelse, etc.) – dette giver type-sikkerhed og let udvidelse.
// 2. Hver kategori har en listOf<SubPrice> med name (til label) og defaultPrice (til savePrices hvis blank).
// 3. Tilføjet alle kategorier baseret på uploadede screens: FacadePudsning (som Netpuds), Opmuring, Fliser, Badeværelse (fra din input), Omfugning, Nedbrydning, Skorsten, Fundament.
// 4. Udled subpriser logisk: F.eks. Opmuring – "Standard mursten", "Special mursten"; Fliser – lignende badeværelse med størrelser; Nedbrydning – "Indvendig væg", "Udvendig væg"; Skorsten – "Reparation", "Ny"; Fundament – "Standard", "Armeret".
// 5. Tilføjet note om klinker/fliser ikke inkluderet i Badværelse og Fliser via isNoteRequired: Boolean – vises i UI.
// 6. allCategories: List<PriceCategory> for loop i Screen.
// 7. Defaults: Eksempler – juster efter markedsdata; 0f hvis ukendt.
// 8. Fuldt funktionsdygtig – brug i Screen og ViewModel for dynamisk generering.

package dk.byggepiloten.firma.data.model.price

// Sealed class for kategorier – udvid med flere hvis nødvendigt
sealed class PriceCategory(val name: String, val subPrices: List<SubPrice>, val isNoteRequired: Boolean = false)

data class SubPrice(val name: String, val defaultPrice: Float)

object PriceCategories {
    val allCategories = listOf(
        Netpuds,
        Omfugning,
        Badværelse,
        FacadePudsning,
        Opmuring,
        Fliser,
        Nedbrydning,
        Skorsten,
        Fundament
    )

    object Netpuds : PriceCategory(
        name = "Netpuds",
        subPrices = listOf(
            SubPrice("Standard grå", 200f),
            SubPrice("Anden farve", 250f)
        )
    )

    object Omfugning : PriceCategory(
        name = "Omfugning",
        subPrices = listOf(
            SubPrice("Trykket med pind", 150f),
            SubPrice("Trykket med kugle/rør", 160f),
            SubPrice("Tilbagelagt fuge", 170f),
            SubPrice("Brændte fuger", 180f)
        )
    )

    object Badværelse : PriceCategory(
        name = "Badværelse",
        subPrices = listOf(
            SubPrice("Total Renovering", 1000f),
            SubPrice("Vådrumssikring", 300f),
            SubPrice("Fjern gamle fliser på væg", 100f),
            SubPrice("Fjern gamle klinker på gulv", 120f),
            SubPrice("Pudsning af væg", 200f),
            SubPrice("Opretning af gulv (Flydespartel)", 150f),
            SubPrice("Klinker på Gulv: 10x10 til 20x20", 400f),
            SubPrice("Klinker på Gulv: 30x30 til 30x60", 450f),
            SubPrice("Klinker på Gulv: 60x60 til ?", 500f),
            SubPrice("Fliser på væg: 10x10 til 20x20", 350f),
            SubPrice("Fliser på væg: 30x30 til 30x60", 400f),
            SubPrice("Fliser på væg: 60x60 til ?", 450f),
            SubPrice("Opbygning af skillevæg", 600f),
            SubPrice("Reparation af eksisterende murværk", 250f)
        ),
        isNoteRequired = true // Tilføjet for note om klinker/fliser
    )

    object FacadePudsning : PriceCategory(
        name = "Facade Pudsning",
        subPrices = listOf(
            SubPrice("Standard puds", 220f),
            SubPrice("Farvet puds", 270f),
            SubPrice("Isoleret puds", 300f)
        )
    )

    object Opmuring : PriceCategory(
        name = "Opmuring",
        subPrices = listOf(
            SubPrice("Standard mursten", 500f),
            SubPrice("Special mursten (f.eks. tegl)", 600f),
            SubPrice("Armeret opmuring", 550f)
        )
    )

    object Fliser : PriceCategory(
        name = "Fliser",
        subPrices = listOf(
            SubPrice("Standard fliser", 350f),
            SubPrice("10x10 til 20x20", 400f),
            SubPrice("30x30 til 30x60", 450f),
            SubPrice("60x60 til ?", 500f),
            SubPrice("Fjern gamle fliser", 100f)
        ),
        isNoteRequired = true // Tilføjet for note om fliser ikke inkluderet
    )

    object Nedbrydning : PriceCategory(
        name = "Nedbrydning",
        subPrices = listOf(
            SubPrice("Indvendig væg", 150f),
            SubPrice("Udvendig væg", 200f),
            SubPrice("Gulv nedbrydning", 180f),
            SubPrice("Affaldshåndtering", 100f)
        )
    )

    object Skorsten : PriceCategory(
        name = "Skorsten",
        subPrices = listOf(
            SubPrice("Reparation", 800f),
            SubPrice("Ny skorsten", 1500f),
            SubPrice("Fjerne skorsten", 600f),
            SubPrice("Rensning og kontrol", 300f)
        )
    )

    object Fundament : PriceCategory(
        name = "Fundament",
        subPrices = listOf(
            SubPrice("Standard fundament", 400f),
            SubPrice("Armeret fundament", 500f),
            SubPrice("Gravearbejde", 200f),
            SubPrice("Isolering", 250f)
        )
    )
}