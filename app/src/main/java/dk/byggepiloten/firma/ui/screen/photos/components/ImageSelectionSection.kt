// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/photos/components/ImageSelectionSection.kt
// FIX – import FontWeight
// Linjer: 94

package dk.byggepiloten.firma.ui.screen.photos.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.net.Uri
import coil.compose.AsyncImage
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.task.BaseTaskViewModel

@Composable
fun ImageSelectionSection(
    viewModel: BaseTaskViewModel,
    imageUris: List<Uri>,
    modifier: Modifier = Modifier
) {
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris?.let { viewModel.addImages(it) }
    }

    Column(modifier = modifier) {
        Text("Tilføj billeder (anbefales)", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { photoPicker.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text("Vælg billeder fra galleri", color = ByggePilotenBlue)
        }

        Spacer(Modifier.height(16.dp))
        if (imageUris.isNotEmpty()) {
            Text("${imageUris.size} billeder valgt", color = Color.White)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(imageUris, key = { it.toString() }) { uri ->
                    Box {
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                        IconButton(
                            onClick = { viewModel.removeImage(uri) },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(Icons.Default.Delete, null, tint = Color.White)
                        }
                    }
                }
            }
        } else {
            Text("Ingen billeder valgt endnu", color = Color.White.copy(alpha = 0.8f))
        }
    }
}