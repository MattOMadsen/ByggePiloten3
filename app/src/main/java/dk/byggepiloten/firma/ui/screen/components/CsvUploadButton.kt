// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/CsvUploadButton.kt
// Ny fil: Udtaget CSV-upload-knap fra FirmaPriceSetupScreen.kt.
// Trin-for-trin: 1. Modtag onClick. 2. Vis Button med ikon og tekst.
package dk.byggepiloten.firma.ui.screen.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CsvUploadButton(onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Default.UploadFile, null)
        Spacer(Modifier.width(8.dp))
        Text("Upload din CSV-prisliste")
    }
}