// app/src/main/java/dk/byggepiloten/firma/ui/screen/SupplierDropdown.kt
// OPDATERET: Rettet deprecation – tilføjet menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true).
// Beholdt alt andet 100% uændret.

package dk.byggepiloten.firma.ui.screen.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierDropdown(
    selectedSupplier: String,
    onChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedSupplier,
            onValueChange = {},
            readOnly = true,
            label = { Text("Vælg leverandør") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()  // RETTET: Tilføjet ny overload – løser deprecation.
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf("Stark", "XL-Byg", "Bygma", "Anden").forEach { supplier ->
                DropdownMenuItem(
                    text = { Text(supplier) },
                    onClick = {
                        onChange(supplier)
                        expanded = false
                        Timber.d("Leverandør valgt: $supplier")
                    }
                )
            }
        }
    }
}