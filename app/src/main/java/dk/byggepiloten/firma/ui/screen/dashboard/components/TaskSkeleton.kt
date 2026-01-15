// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/dashboard/components/TaskSkeleton.kt
// Ny reusable loading skeleton til dashboard (grå placeholders)
// Vises mens data loader – 5 stk typisk

package dk.byggepiloten.firma.ui.screen.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme

@Composable
fun TaskSkeleton(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Placeholder billede
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Titel placeholder
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .width(200.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                )

                Spacer(Modifier.height(12.dp))

                // Info row placeholder
                Row {
                    Box(
                        modifier = Modifier
                            .size(width = 60.dp, height = 20.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                    )
                    Spacer(Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(width = 80.dp, height = 20.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Pris placeholder
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .width(150.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                )
            }
        }
    }
}