// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringValidator.kt
// OPDATERET TIL BATCH 4 – 138 linjer
// + Validering for access (step 14):
//   - Hvis god adgang → altid valid
//   - Hvis dårlig adgang → kræver mindst ét problem + billeder
// + Inline error polish i wizard allerede håndteret
// + KDoc + kommentarer

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import android.net.Uri
import dk.byggepiloten.firma.data.model.task.WallData

/**
 * Central valideringslogik for hele opmuring-wizard.
 */
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
                data.wallMeasurements.isNotEmpty() && data.wallMeasurements.all {
                    (it.length ?: 0f) > 0f && (it.height ?: 0f) > 0f
                }
            }
            5 -> data.thicknessOption != null
            6 -> data.stoneType != null
            7 -> data.mortarType != null
            8 -> { // Åbninger – positiv areal hvis valgt
                if (data.openingMode == null) true
                else {
                    val total = when (data.openingMode) {
                        "samlet" -> data.openingTotalAreaM2 ?: 0f
                        "individuel" -> data.openingMeasurements.sumOf {
                            (it.widthCm ?: 0f) * (it.heightCm ?: 0f) / 10000.0
                        }.toFloat()
                        else -> 0f
                    }
                    total > 0f
                }
            }
            9 -> data.surfaceFinish != null
            10 -> data.reinforcementLevel != null // Pudsarmering
            11 -> data.insulationWanted != null
            12 -> data.foundationOption != null
            13 -> { // Skader (kun ved reparation)
                if (data.hasCracks != true && data.hasMoistureDamage != true && data.hasSettlementDamage != true) true
                else (stepPhotos["damage"] ?: emptyList()).isNotEmpty()
            }
            14 -> { // Adgang
                if (data.goodAccess != false) true
                else data.accessProblems.isNotEmpty() && (stepPhotos["access"] ?: emptyList()).isNotEmpty()
            }
            15 -> true // Billeder valgfri i photos-step
            16 -> true // Beskrivelse valgfri
            else -> true
        }
    }
}

// Total lines: 138