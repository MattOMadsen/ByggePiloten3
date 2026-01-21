// Fil: app/src/main/java/dk/byggepiloten/firma/data/model/price/PriceCategories.kt
// OPDATERET: FacadePudsning ændret til Pudsning for konsistens i hele appen.

package dk.byggepiloten.firma.data.model.price

sealed class PriceCategory(val id: String, val name: String) {
    object Opmuring : PriceCategory("opmuring", "Opmuring")
    object Pudsning : PriceCategory("pudsning", "Pudsning") // Ændret fra FacadePudsning
    object FliseKlinke : PriceCategory("flise_klinke", "Flise- og klinkearbejde")
    object Badevaerelse : PriceCategory("badeværelse", "Badeværelse")
    object Omfugning : PriceCategory("omfugning", "Omfugning")
    object Nedbrydning : PriceCategory("nedbrydning", "Nedbrydning")
    object Skorsten : PriceCategory("skorsten", "Skorstensarbejde")
    object Fundament : PriceCategory("fundament", "Fundament")

    companion object {
        val all = listOf(
            Opmuring,
            Pudsning,
            FliseKlinke,
            Badevaerelse,
            Omfugning,
            Nedbrydning,
            Skorsten,
            Fundament
        )

        fun fromId(id: String): PriceCategory? = all.find { it.id == id }
    }
}
