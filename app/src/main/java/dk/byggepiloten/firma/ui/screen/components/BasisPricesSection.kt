// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/BasisPricesSection.kt
// Ny fil: Udtaget basispris-felter fra FirmaPriceSetupScreen.kt.
// Trin-for-trin: 1. Modtag states og onChange-funktioner. 2. Vis felter med suffixes og enabled baseret på selectedSource. 3. Inkluder profitPct med info-ikon.
package dk.byggepiloten.firma.ui.screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.price.PriceSource

@Composable
fun BasisPricesSection(
    hourlyRate: String,
    onHourlyChange: (String) -> Unit,
    overtimeRate: String,
    onOvertimeChange: (String) -> Unit,
    drivingPerKm: String,
    onDrivingChange: (String) -> Unit,
    profitPct: String,
    onProfitChange: (String) -> Unit,
    selectedSource: PriceSource,
    onInfoClick: () -> Unit
) {
    OutlinedTextField(
        value = hourlyRate,
        onValueChange = { onHourlyChange(it.filter { it.isDigit() || it == '.' }) },
        label = { Text("Normal timepris") },
        suffix = { Text("kr/time") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        enabled = selectedSource == PriceSource.MANUAL || selectedSource == PriceSource.STANDARD
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = overtimeRate,
        onValueChange = { onOvertimeChange(it.filter { it.isDigit() || it == '.' }) },
        label = { Text("Overarbejde") },
        suffix = { Text("kr/time") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        enabled = selectedSource == PriceSource.MANUAL
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = drivingPerKm,
        onValueChange = { onDrivingChange(it.filter { it.isDigit() || it == '.' }) },
        label = { Text("Kørsel pr. km") },
        suffix = { Text("kr/km") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        enabled = selectedSource == PriceSource.MANUAL
    )
    Spacer(Modifier.height(8.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = profitPct,
            onValueChange = { input ->
                input.filter { it.isDigit() || it == '.' }.toFloatOrNull()?.takeIf { it in 0f..100f }?.let { onProfitChange(input) }
            },
            label = { Text("Fortjeneste på materialer") },
            suffix = { Text("%") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f),
            enabled = selectedSource == PriceSource.MANUAL
        )
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onInfoClick) {
            Icon(Icons.Default.Info, "Info")
        }
    }
}