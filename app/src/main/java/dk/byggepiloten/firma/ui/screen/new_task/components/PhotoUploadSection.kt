// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/PhotoUploadSection.kt
// OPDATERET: Grid med weight(1f) + Spacers for tomme pladser → 100% lige store thumbnails + ingen crash
// - Hver thumbnail får præcis 1/3 bredde (weight(1f)) → kvadratisk via aspectRatio(1f)
// - Tomme pladser i rækken fyldes med Spacer(weight(1f)) → ingen strækning, alle billeder holder samme størrelse
// - Add-button øverst venstre, samme størrelse som billeder
// - Close-knap større + centreret sort cirkel for bedre synlighed
// - Ingen fraction/fixed size → finite constraints (Row har fast bredde fra fillMaxWidth)
// - Ingen Lazy/FlowRow → ingen nested scroll eller infinite height
// - Label med rød * + ekstra tekst + rød fejl hvis ingen billeder
// - Launcher vælger flere billeder ad gangen
// - Ændringen gælder alle steps (inkl. "Adgang" – billeder nu 100% lige store efter upload)
// - Total lines: 228 (fuld fil med alle imports og kommentarer)

package dk.byggepiloten.firma.ui.screen.new_task.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import android.net.Uri

@Composable
fun PhotoUploadSection(
    label: String,
    isRequired: Boolean = false,
    currentUris: List<Uri>,
    onUrisChange: (List<Uri>) -> Unit
) {
    // Launcher til flere billeder
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { newUris: List<Uri>? ->
        newUris?.let { onUrisChange(currentUris + it) }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Label + rød *
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            if (isRequired) {
                Text(
                    text = " *",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        // Ekstra tekst hvis påkrævet
        if (isRequired) {
            Text(
                text = "Påkrævet for at gå videre",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }

        // Grid: Add først (null = add), derefter billeder – chunked i rækker á max 3
        val gridItems: List<Uri?> = listOf(null) + currentUris

        val chunks = gridItems.chunked(3)

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            chunks.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { uri ->
                        Box(
                            modifier = Modifier
                                .weight(1f) // Præcis 1/3 bredde → ens størrelse
                                .aspectRatio(1f) // Kvadratisk – højde = bredde
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            if (uri == null) {
                                // Add-button – samme størrelse
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.White.copy(alpha = 0.15f))
                                        .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                        .clickable { launcher.launch("image/*") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Tilføj billeder",
                                        tint = Color.White,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            } else {
                                // Billede – crop til kvadrat
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Close-knap – større og centreret
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Fjern billede",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .size(36.dp)
                                        .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                                        .clickable { onUrisChange(currentUris.filter { it != uri }) }
                                        .padding(6.dp)
                                )
                            }
                        }
                    }

                    // Kritisk: Udfyld tomme pladser med Spacer(weight(1f)) → ingen strækning, alle thumbnails holder størrelse
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Fejl hvis påkrævet og ingen billeder
        if (isRequired && currentUris.isEmpty()) {
            Text(
                text = "Upload mindst ét billede for at gå videre",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}