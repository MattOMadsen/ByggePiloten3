package dk.byggepiloten.firma.ui.screen.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PhotoGrid(
    photos: List<String>,
    title: String? = null,
    modifier: Modifier = Modifier
) {
    if (photos.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 3,
            modifier = Modifier.fillMaxWidth()
        ) {
            photos.forEach { url ->
                val itemModifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                
                Box(modifier = itemModifier) {
                    SubcomposeAsyncImage(
                        model = url,
                        contentDescription = "Billede",
                        loading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.LightGray.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.LightGray.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Fejl",
                                    tint = Color.Gray
                                )
                            }
                        },
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            // Fill remaining space if less than 3 items to keep grid consistent
            repeat(3 - (photos.size % 3)) {
                if (photos.size % 3 != 0) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
