// File: app/src/main/java/dk/byggepiloten/firma/data/model/WallData.kt

/**
 * Simpel data class for væg-data i måleguide (SRP: Kun holder væg-specifikke felter).
 * Bruges i TaskViewModel og CustomerWizardScreen for dynamisk væg-tilføjelse.
 * ID: Unik identifikator (brug counter eller UUID for enkelhed).
 * Fratræk: Areal at fratrække (f.eks. vinduer/døre fra WindowDoorHelper-logik).
 */
package dk.byggepiloten.firma.data.model

data class WallData(
    val id: Int,
    var length: String = "",
    var height: String = "",
    var fratræk: Float = 0f  // Fratræk for vinduer/døre
)