// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/common/StyledTextField.kt
// FULD FIX – minimerer blå paste-firkant (autoCorrect = false + imeAction = Next)
// Linjer: 68

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
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = ByggePilotenBlue) },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
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
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next,
            autoCorrect = false
        ),
        shape = MaterialTheme.shapes.medium
    )
}