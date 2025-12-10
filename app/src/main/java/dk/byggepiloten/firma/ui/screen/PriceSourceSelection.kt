// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/PriceSourceSelection.kt
// Ny fil: Udtaget radio-buttons for pris-kilde fra FirmaPriceSetupScreen.kt.
// Trin-for-trin: 1. Modtag selectedSource og onChange. 2. Loop gennem PriceSource.values(). 3. Vis radio + label + description.
package dk.byggepiloten.firma.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.data.model.PriceSource

@Composable
fun PriceSourceSelection(
    selectedSource: PriceSource,
    onChange: (PriceSource) -> Unit
) {
    PriceSource.values().forEach { source ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selectedSource == source, onClick = { onChange(source) })
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = when (source) {
                        PriceSource.STANDARD -> "Hent fra leverandør (anbefalet)"
                        PriceSource.MANUAL -> "Indtast manuelt"
                        PriceSource.CSV -> "Upload din egen CSV"
                    },
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                )
                Text(
                    text = when (source) {
                        PriceSource.STANDARD -> "Vi henter aktuelle priser fra Stark, XL-Byg eller Bygma"
                        PriceSource.MANUAL -> "Fuldt kontrol – du indtaster alt selv"
                        PriceSource.CSV -> "Upload din egen prisliste (kategori;pris pr. linje)"
                    },
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}