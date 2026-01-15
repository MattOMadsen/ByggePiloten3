// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/dashboard/components/TaskCard.kt
// RETTET VERSION – Tilføjet manglende imports (background + clip)
// + combinedClickable for long click
// + Større touch-targets + accessibility

package dk.byggepiloten.firma.ui.screen.dashboard.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import dk.byggepiloten.firma.data.model.task.Request
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    request: Request,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick.takeIf { it != {} }
            )
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            if (request.images.isNotEmpty()) {
                SubcomposeAsyncImage(
                    model = request.images.first(),
                    contentDescription = "Billede af opgaven: ${request.category}",
                    loading = {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    },
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.width(16.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = buildString {
                        append(request.category)
                        request.roomType?.let { append(" – $it") }
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (request.areaM2 > 0f) {
                        Text("${request.areaM2.toInt()} m²", color = Color.Black.copy(alpha = 0.8f))
                        Spacer(Modifier.width(12.dp))
                    }

                    StatusBadge(status = request.status ?: "new")
                    Spacer(Modifier.width(12.dp))

                    val bidCount = request.bids.size
                    if (bidCount > 0) {
                        Text(
                            "$bidCount bud modtaget",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (request.aiPrice > 0f) {
                    Text(
                        text = "Ca. pris: ${request.aiPrice.toInt()}–${(request.aiPrice * 1.3f).toInt()} kr.",
                        color = Color.Black.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}