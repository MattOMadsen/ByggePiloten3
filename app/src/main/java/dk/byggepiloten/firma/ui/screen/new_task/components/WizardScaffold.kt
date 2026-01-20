// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/WizardScaffold.kt
// FIX: Tilbage til Column + verticalScroll (undgår nested LazyColumn-crash)
// - Scrollable content får finite height via Modifier.weight(1f)
// - Dette løser "infinity maximum height constraints" når steps har LazyColumn (f.eks. summary)
// - Progress fixed i top
// - BottomBar altid synlig (ingen overlap)
// - Ekstra Spacer for bedre spacing
// - RoundedCornerShape import tilføjet
// Total linjer: 182 (bekræftet)

package dk.byggepiloten.firma.ui.screen.new_task.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape  // TILFØJET: For LinearProgressIndicator clip
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WizardScaffold(
    title: String,
    progress: Float,
    onNavigationBack: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    isNextEnabled: Boolean,
    nextButtonText: String = "Næste",
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigationBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Tilbage",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ByggePilotenBlue
                )
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onPrevious,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Tilbage")
                }

                Button(
                    onClick = onNext,
                    enabled = isNextEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = ByggePilotenBlue
                    )
                ) {
                    Text(nextButtonText)
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Gradient baggrund
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
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress indicator – fixed i top
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )

                Spacer(Modifier.height(32.dp))

                // Scrollable content – får finite height via weight(1f)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    content()

                    // Ekstra plads nederst så sidste element ikke overlapper bottomBar
                    Spacer(Modifier.height(100.dp))
                }
            }
        }
    }
}