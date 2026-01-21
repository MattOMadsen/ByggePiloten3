// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsValidator.kt
// RETTET – conditional validering baseret på inde/ude (uændret logik, men sikrer kompatibilitet)

package dk.byggepiloten.firma.ui.screen.new_task.categories.puds

import android.net.Uri
import dk.byggepiloten.firma.data.model.task.PudsData

object PudsValidator {

    fun isStepValid(
        data: PudsData,
        stepPhotos: Map<String, List<Uri>>,
        stepNumber: Int
    ): Boolean {
        return when (stepNumber) {
            1 -> !data.indeUde.isNullOrBlank()
            2 -> data.area?.let { it > 0f } == true
            3 -> !data.vaegtype.isNullOrBlank()
            4 -> data.indeUde == "Inde" || data.hojde?.let { it > 0f } == true
            5 -> data.indeUde == "Inde" || data.stilladsNoedvendigt != null
            6 -> listOf(data.underlagRevner, data.underlagFugt).all { !it.isNullOrBlank() } &&
                    (data.indeUde == "Inde" || !data.underlagGammelPuds.isNullOrBlank())
            7 -> data.indeUde == "Inde" || !data.vejretidspunkt.isNullOrBlank()
            8 -> data.indeUde == "Inde" || (!data.armeringsnet.isNullOrBlank() && !data.isolering.isNullOrBlank())
            9 -> !data.haeftemoertelType.isNullOrBlank()
            else -> true
        }
    }
}