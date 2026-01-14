// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringValidator.kt
// FULD FIX – Strings matcher nu præcis UI ("Samlet areal" / "Individuelle vægge")
// wallMode != null check beholdt (LaunchedEffect i DimensionsStep sikrer det fra start)
// Streng validering – ingen lenient workaround mere
// Linjer: 88

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import android.net.Uri
import dk.byggepiloten.firma.data.model.task.WallData

object OpmuringValidator {

    fun isStepValid(
        data: WallData,
        stepPhotos: Map<String, List<Uri>>,
        stepNumber: Int
    ): Boolean {
        return when (stepNumber) {
            1 -> data.murType != null

            2 -> data.isRepair != null

            3 -> data.bearingWall != null

            4 -> {
                // Kræver wallMode valgt (sikret via LaunchedEffect i step)
                if (data.wallMode == null) return false

                if (data.wallMode == "Samlet areal") {
                    (data.wallTotalAreaM2 ?: 0f) > 0f
                } else { // "Individuelle vægge"
                    data.wallMeasurements.isNotEmpty() && data.wallMeasurements.all {
                        (it.length ?: 0f) > 0f && (it.height ?: 0f) > 0f
                    }
                }
            }

            8 -> {
                if (data.openingMode == null) true
                else if (data.openingMode == "Samlet areal") (data.openingTotalAreaM2 ?: 0f) > 0f
                else data.openingMeasurements.isNotEmpty()
            }

            12 -> data.foundationOption != null

            13 -> {
                if (data.hasCracks != true && data.hasMoistureDamage != true && data.hasSettlementDamage != true) true
                else (stepPhotos["damage"] ?: emptyList()).isNotEmpty()
            }

            14 -> {
                if (data.goodAccess != false) true
                else data.accessProblems.isNotEmpty() && (stepPhotos["access"] ?: emptyList()).isNotEmpty()
            }

            else -> true
        }
    }
}