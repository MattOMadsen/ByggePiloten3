// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/photos/components/DescriptionSection.kt
// UÆNDRET – multi-line beskrivelse
// Linjer: 56

package dk.byggepiloten.firma.ui.screen.photos.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField

@Composable
fun DescriptionSection(
    description: String,
    onDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Tilføj beskrivelse (anbefales)",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = MaterialTheme.typography.titleMedium.fontWeight
        )
        Spacer(Modifier.height(16.dp))
        StyledTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = "Beskriv opgaven detaljeret (f.eks. placering, ønsker, problemer)",
            singleLine = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}