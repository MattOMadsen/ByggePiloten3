// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/NewTaskWizardScreen.kt
// OPDATERET – "Facadepudsning" fjernet, ny "Pudsning" tilføjet (route "pudsning")
// Ikon: FormatPaint (passende til pudsning)
// Subtitle: "Pudsning af vægge – inde eller ude"

package dk.byggepiloten.firma.ui.screen.new_task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import timber.log.Timber

data class TaskCategory(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskWizardScreen(navController: NavController) {
    val categories = listOf(
        TaskCategory("Opmuring", "Opførelse af nye vægge", Icons.Default.Wallpaper, "opmuring"),
        TaskCategory("Pudsning", "Pudsning af vægge – inde eller ude", Icons.Default.FormatPaint, "pudsning"), // NY – erstatter Facadepudsning
        TaskCategory("Flise- og klinkearbejde", "Fliser på gulv og vægge", Icons.Default.GridOn, "flise_klinke"),
        TaskCategory("Badeværelse", "Komplet eller delvis renovering", Icons.Default.Bathtub, "badeværelse"),
        TaskCategory("Omfugning", "Omfugning af murværk", Icons.Default.Build, "omfugning"),
        TaskCategory("Nedbrydning", "Nedrivning af vægge og fliser", Icons.Default.DeleteForever, "nedbrydning"),
        TaskCategory("Skorstensarbejde", "Reparation eller opbygning", Icons.Default.LocalFireDepartment, "skorsten"),
        TaskCategory("Fundament", "Støbning og reparation", Icons.Default.Foundation, "fundament")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ByggePilotenBlue,
                        Color(0xFF42A5F5),
                        Color(0xFF90CAF9)
                    )
                )
            )
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Ny opgave",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tilbage", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = ByggePilotenBlue,
                        titleContentColor = Color.White
                    )
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    TaskCategoryCard(category = category) {
                        try {
                            Timber.d("Navigated to ${category.route}")
                            navController.navigate(category.route)
                        } catch (e: Exception) {
                            Timber.e(e, "Navigation fejl for ${category.route} – tjek NavHost")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskCategoryCard(category: TaskCategory, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()   // Ændret fra fillMaxSize() → bedre i grid
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = category.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = category.subtitle,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}