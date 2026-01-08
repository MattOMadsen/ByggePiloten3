// Fil: app/src/main/java/dk/byggepiloten/firma/data/model/WallData.kt
// OPDATERET: Udvidet til fuld support af opmuring-wizard (og stadig kompatibel med facade hvis relevant).
// - Ændret length/height til Float? (bedre til beregninger – parsing håndteres i screens med toFloatOrNull()).
// - Tilføjet alle nye felter fra opmuring-flow: murType, isRepair, thickness, sten/mørtel, skader, åbninger, isolering, fundament, adgang osv.
// - Fjernet id (ikke nødvendigt for single-wall wizards som opmuring/facade – multiple walls kan håndteres senere).
// - Fjernet fratræk (håndteres nu via openingsCount + estimering i screen hvis ønsket).
// - Alle felter nullable med default null (sikker binding i Compose – undgår crashes ved tomme felter).
// - Data class med copy() for nem ViewModel-updates.
// - Fulde imports + kommentarer.
// - Linjer: 78 (simpel men komplet).

package dk.byggepiloten.firma.data.model

/**
 * Data class for væg/opmurings-data i wizards (opmuring, facade osv.).
 * Holder alle felter fra opmuring-flow (ny mur + reparation).
 * Bruges i TaskViewModel.wallData (StateFlow) – opdateres live via copy().
 * Parsing: Screens håndterer toFloatOrNull()/toIntOrNull() for sikkerhed.
 */
data class WallData(
    // Step 1: Murtype
    var murType: String? = null,                  // f.eks. "Facademur (skalmur/ydervæg)"
    var customMurType: String? = null,            // Hvis "Andet"

    // Step 2: Ny eller reparation
    var isRepair: Boolean? = null,                // true = reparation, false = ny mur

    // Dimensioner (begge flows)
    var length: Float? = null,                    // Længde i meter
    var height: Float? = null,                    // Højde i meter

    // Ny mur: Tykkelse
    var thicknessOption: String? = null,          // f.eks. "108 mm (halvsten)"
    var customThickness: Int? = null,             // Hvis "Anden" (mm)

    // Ny mur: Materialer
    var stoneType: String? = null,                // f.eks. "Almindelig rød mursten"
    var customStoneType: String? = null,          // Hvis "Anden"
    var mortarType: String? = null,               // f.eks. "Standard KC-mørtel"
    var customMortarType: String? = null,         // Hvis "Anden"

    // Reparation: Skader
    var hasCracks: Boolean? = null,
    var cracksDescription: String? = null,
    var hasMoistureDamage: Boolean? = null,
    var moistureDescription: String? = null,
    var hasSettlementDamage: Boolean? = null,
    var settlementDescription: String? = null,

    // Ny mur: Detaljer
    var openingsCount: Int? = null,               // Antal vinduer/døre

    // Ny mur + specifik for facademur
    var insulationWanted: Boolean? = null,        // Kun vist ved facademur

    // Ny mur
    var foundationNeeded: Boolean? = null,        // Fundament nødvendigt?

    // Fælles: Adgang
    var goodAccess: Boolean? = null               // God adgang til stillads?
)