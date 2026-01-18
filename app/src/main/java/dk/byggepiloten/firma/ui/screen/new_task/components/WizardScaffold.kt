// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/components/WizardScaffold.kt
// FULD RETTET – Icons import tilføjet for ArrowBack
// Experimental API beholdt (LinearProgressIndicator progress = { } er Material3)

package dk.byggepiloten.firma.ui.screen.new_task.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigationBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Tilbage",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ByggePilotenBlue
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            ByggePilotenBlue,
                            ByggePilotenBlue.copy(alpha = 0.7f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .statusBarsPadding()
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    color = Color.White,
                    trackColor = Color.Transparent
                )

                content()

                Spacer(Modifier.weight(1f))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onPrevious,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White,
                            containerColor = Color.Transparent
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Color.White, Color.White))
                        )
                    ) {
                        Text("Tilbage")
                    }

                    Button(
                        onClick = onNext,
                        enabled = isNextEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = ByggePilotenBlue,
                            disabledContainerColor = Color.White.copy(alpha = 0.3f),
                            disabledContentColor = ByggePilotenBlue.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(nextButtonText)
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}