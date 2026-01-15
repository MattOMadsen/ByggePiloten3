// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/dashboard/components/TaskSection.kt
// RETTET VERSION – Tilføjet onLongClick parameter (for slet med undo)
// + Fuld imports
// + Conditional visibility håndteres udefra (kun kald hvis tasks.isNotEmpty())

package dk.byggepiloten.firma.ui.screen.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.byggepiloten.firma.data.model.task.Request

@Composable
fun TaskSection(
    title: String,
    tasks: List<Request>,
    onTaskClick: (Request) -> Unit,
    onLongClick: (Request) -> Unit = {}, // Ny parameter
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks) { request ->
                TaskCard(
                    request = request,
                    onClick = { onTaskClick(request) },
                    onLongClick = { onLongClick(request) }
                )
            }
        }
    }
}