// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/common/StyledTextField.kt
// OPDATERET – tilføjet valgfri keyboardOptions-parameter for fuld fleksibilitet
// (løser fejl i PudsAreaStep, PudsHoejdeStep osv.)

package dk.byggepiloten.firma.ui.screen.new_task.components.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,           // Beholdes for bagudkompatibilitet
    keyboardOptions: KeyboardOptions = KeyboardOptions(       // NY – fuld override mulig
        keyboardType = keyboardType,
        imeAction = if (keyboardType == KeyboardType.Text) ImeAction.Next else ImeAction.Done,
        autoCorrect = false
    ),
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else 10
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = ByggePilotenBlue) },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        maxLines = maxLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedBorderColor = ByggePilotenBlue,
            unfocusedBorderColor = Color.LightGray,
            cursorColor = ByggePilotenBlue,
            focusedLabelColor = ByggePilotenBlue,
            unfocusedLabelColor = ByggePilotenBlue,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            disabledTextColor = Color.Black
        ),
        keyboardOptions = keyboardOptions,  // Bruger nu den sendte (eller default baseret på keyboardType)
        shape = MaterialTheme.shapes.medium
    )
}