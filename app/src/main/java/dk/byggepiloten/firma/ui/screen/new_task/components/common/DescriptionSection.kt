// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/common/DescriptionSection.kt
// FULD RETTET VERSION – compile-fejl fikset
// + Import androidx.compose.material3.OutlinedTextFieldDefaults
// + Korrekt colors() kald
// + Multi-line + enter virker
// + ca. 70 linjer

package dk.byggepiloten.firma.ui.screen.new_task.components.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun DescriptionSection(
    description: String,
    onDescriptionChange: (String) -> Unit,
    label: String = "Tilføj beskrivelse (valgfrit)",
    placeholder: String = "Skriv en detaljeret beskrivelse af opgaven..."
) {
    OutlinedTextField(
        value = description,
        onValueChange = onDescriptionChange,
        label = { Text(label, color = Color.White) },
        placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.7f)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        minLines = 4,
        singleLine = false,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Default
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
            cursorColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
        ),
        shape = MaterialTheme.shapes.medium
    )
}