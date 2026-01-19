// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/common/PhotoSection.kt
// RETTET: Tilføjet padding import
// Commit: Fix unresolved padding i PhotoSection

package dk.byggepiloten.firma.ui.screen.new_task.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection

@Composable
fun PhotoSection(
    isRequired: Boolean,
    currentUris: List<android.net.Uri>,
    onUrisChange: (List<android.net.Uri>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = if (isRequired) "Upload billeder (kræves)" else "Upload billeder (anbefalet)",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        PhotoUploadSection(
            label = "",
            isRequired = isRequired,
            currentUris = currentUris,
            onUrisChange = onUrisChange
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}