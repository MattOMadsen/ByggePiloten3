// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/WizardScaffold.kt
// OPDATERET: Content er nu fuldt scrollable (verticalScroll på indhold)
// BottomBar fixed – "Næste"/"Tilbage" altid synlige nederst, uanset scroll
// Ekstra padding bottom på scroll-content for at undgå overlap med bottomBar
// Progress-indicator flyttet ind i scroll-content for konsistens
// Commit: Fix crash + knapper altid synlige – scroll på content i WizardScaffold
// Linjer: 172 (baseret på original + scroll + bottomBar)

package dk.byggepiloten.firma.ui.screen.new_task.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
        containerColor = Color.Transparent,  // Tillad gradient-baggrund
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
                    .verticalScroll(rememberScrollState())  // FIX: Scroll på hele content – ingen overlap/crash
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 100.dp),  // Ekstra plads til bottomBar
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )

                Spacer(Modifier.height(24.dp))

                content()
            }
        }
    }
}