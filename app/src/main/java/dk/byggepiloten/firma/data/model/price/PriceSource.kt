package dk.byggepiloten.firma.data.model.price

/**
 * Hvor firmaets priser kommer fra
 * STANDARD → Brug appens standardpriser (f.eks. 650 kr/m²)
 * CSV      → Import fra CSV-fil (f.eks. egen prisliste)
 * MANUAL   → Indtast alle priser manuelt i appen
 */
enum class PriceSource {
    STANDARD,
    CSV,
    MANUAL
}