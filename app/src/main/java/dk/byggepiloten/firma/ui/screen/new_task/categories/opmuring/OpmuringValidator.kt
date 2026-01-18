// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringValidator.kt
// FULD OPDATERET – Tilføjet generalImages-parameter (fra BaseTaskViewModel.imageUris)
// Step 15 kræver min. 1 general billede
// RETTET: WallMeasurement felter (length/height)

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import android.net.Uri
import dk.byggepiloten.firma.data.model.task.WallData

object OpmuringValidator {
    fun isStepValid(
        data: WallData,
        stepPhotos: Map<String, List<Uri>>,
        generalImages: List<Uri>,
        stepNumber: Int
    ): Boolean {
        return when (stepNumber) {
            1 -> data.murType != null
            2 -> data.isRepair != null
            3 -> data.bearingWall != null
            4 -> data.wallMeasurements.isNotEmpty() && data.wallMeasurements.all {
                (it.length ?: 0f) > 0f && (it.height ?: 0f) > 0f
            }
            5 -> data.thicknessOption != null
            6 -> data.stoneType != null && (data.stoneType != "Special sten" || !data.specialStoneName.isNullOrBlank())
            7 -> data.mortarType != null
            8 -> true // Åbninger valgfri
            9 -> data.surfaceFinish != null
            10 -> data.reinforcement != null
            11 -> data.insulationWanted != null && (data.insulationWanted == false || data.insulationThickness != null)
            12 -> data.foundationOption != null
            13 -> {
                val hasDamage = data.hasCracks == true || data.hasMoistureDamage == true || data.hasSettlementDamage == true
                if (hasDamage) (stepPhotos["damage"] ?: emptyList()).isNotEmpty() else true
            }
            14 -> {
                if (data.goodAccess == false && data.accessProblems.isNotEmpty()) {
                    (stepPhotos["access"] ?: emptyList()).isNotEmpty()
                } else true
            }
            15 -> generalImages.isNotEmpty()
            16 -> true
            17 -> true
            else -> true
        }
    }
}