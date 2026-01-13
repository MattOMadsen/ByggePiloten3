// File: app/src/main/java/dk/byggepiloten/firma/data/model/ImportMode.kt
package dk.byggepiloten.firma.data.misc

/**
 * ImportMode – styrer hvordan CSV-import håndterer eksisterende data
 * ADD_ONLY     → Tilføj kun nye rækker (default)
 * OVERWRITE    → Overskriv eksisterende materialer med samme nøgle
 * REPLACE_ALL  → Slet alt og indsæt nyt (farlig – brug med omtanke)
 */
enum class ImportMode {
    ADD_ONLY,
    OVERWRITE,
    REPLACE_ALL
}