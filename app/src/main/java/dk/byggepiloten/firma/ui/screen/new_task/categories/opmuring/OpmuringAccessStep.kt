// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/opmuring/OpmuringAccessStep.kt
// OPDATERET: Compile-fix + hjælpetekst til multi-select
// - Tilføjet import androidx.compose.foundation.background (løser Unresolved reference 'background')
// - Ny hjælpetekst: "Vælg alle relevante problemer (du kan vælge flere)" – placeret lige over bokse for at undgå forvirring
// - Tekst stil: bodyMedium + hvid med alpha (samme som beskrivelsestekst ovenfor)
// - Ingen andre ændringer – MultiChoiceBoxColumn bruges uændret
// - Total lines: 192

package dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring

import androidx.compose.foundation.background // <-- TILFØJET: Løser compile-fejl
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.common.ChoiceBoxColumn
import dk.byggepiloten.firma.ui.screen.new_task.components.common.MultiChoiceBoxColumn
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.viewmodel.task.OpmuringTaskViewModel

@Composable
fun OpmuringAccessStep(
    viewModel: OpmuringTaskViewModel
) {
    val data by viewModel.wallData.collectAsStateWithLifecycle()
    val stepPhotos by viewModel.stepPhotos.collectAsStateWithLifecycle()
    val accessPhotos = stepPhotos["access"] ?: emptyList()

    val hasProblems = data.accessProblems.isNotEmpty()
    val hasPhotos = accessPhotos.isNotEmpty()
    val showError = data.goodAccess == false && (!hasProblems || !hasPhotos)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Adgang til arbejdsstedet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = "Er der god adgang til muren med maskiner, stillads eller kran? Dette påvirker prisen.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.9f)
        )

        // Ja/Nej – single select (uændret)
        val options = listOf("Ja – god adgang", "Nej – dårlig adgang")

        ChoiceBoxColumn(
            options = options,
            selectedOption = when (data.goodAccess) {
                true -> "Ja – god adgang"
                false -> "Nej – dårlig adgang"
                null -> null
            },
            onOptionSelected = { selected ->
                val goodAccess = selected == "Ja – god adgang"
                viewModel.updateWallData(
                    data.copy(
                        goodAccess = goodAccess,
                        accessProblems = if (goodAccess) emptyList() else data.accessProblems,
                        accessCustomDescription = if (goodAccess) null else data.accessCustomDescription
                    )
                )
            }
        )

        if (data.goodAccess == false) {
            Text(
                text = "Hvilke problemer er der med adgangen?",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            // Hjælpetekst – gør det klart at man kan vælge flere
            Text(
                text = "Vælg alle relevante problemer (du kan vælge flere)",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )

            // Multi-select problemer – reusable composable
            val problemOptions = listOf(
                "Ingen parkeringsmulighed",
                "Smalt indkørsel",
                "Trappe/kuperet terræn",
                "Ingen plads til stillads",
                "Kran nødvendig",
                "Andet"
            )

            MultiChoiceBoxColumn(
                options = problemOptions,
                selectedOptions = data.accessProblems.toSet(),
                onToggle = { problem ->
                    val currentProblems = data.accessProblems.toMutableList()
                    if (currentProblems.contains(problem)) {
                        currentProblems.remove(problem)
                    } else {
                        currentProblems.add(problem)
                    }

                    // Clear custom tekst hvis "Andet" fravælges
                    val newDescription = if (problem == "Andet" && !currentProblems.contains("Andet")) {
                        null
                    } else {
                        data.accessCustomDescription
                    }

                    viewModel.updateWallData(
                        data.copy(
                            accessProblems = currentProblems.toList(),
                            accessCustomDescription = newDescription
                        )
                    )
                }
            )

            // Tekstfelt kun ved "Andet"
            if (data.accessProblems.contains("Andet")) {
                StyledTextField(
                    value = data.accessCustomDescription ?: "",
                    onValueChange = {
                        viewModel.updateWallData(data.copy(accessCustomDescription = it))
                    },
                    label = "Beskriv det nærmere",
                    singleLine = false
                )
            }

            PhotoUploadSection(
                label = "Upload billeder der viser adgangsproblemer (obligatorisk ved dårlig adgang)",
                isRequired = false,
                currentUris = accessPhotos,
                onUrisChange = { viewModel.updateStepPhotos("access", it) }
            )

            if (showError) {
                Text(
                    text = "Ved dårlig adgang skal du vælge mindst ét problem og uploade mindst ét billede for at gå videre.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(16.dp)
                )
            }
        }
    }
}