// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/MaterialPricesSection.kt
// Ny fil: Udtaget materiale-felter fra FirmaPriceSetupScreen.kt.
// Trin-for-trin: 1. Modtag pricesToShow map, selectedSource og onChange. 2. Loop gennem entries for felter.
package dk.byggepiloten.firma.ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import dk.byggepiloten.firma.data.model.PriceSource

@Composable
fun MaterialPricesSection(
    pricesToShow: Map<String, String>,
    selectedSource: PriceSource,
    onMaterialChange: (String, String) -> Unit
) {
    androidx.compose.foundation.lazy.LazyColumn {
        items(pricesToShow.entries.toList()) { entry ->
            OutlinedTextField(
                value = entry.value,
                onValueChange = { if (selectedSource == PriceSource.MANUAL) onMaterialChange(entry.key, it.filter { it.isDigit() || it == '.' }) },
                label = { Text(entry.key) },
                suffix = { Text("kr") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedSource == PriceSource.MANUAL
            )
        }
    }
}