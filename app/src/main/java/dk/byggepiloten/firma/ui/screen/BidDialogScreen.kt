// app/src/main/java/dk/byggepiloten/firma/ui/screen/BidDialogScreen.kt
// OPDATERET: Rettet deprecation – brug Locale.forLanguageTag("da-DK").
// Beholdt alt andet 100% uændret.

package dk.byggepiloten.firma.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.ui.viewmodel.BidViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BidDialogScreen(
    requestId: String,
    onDismiss: () -> Unit,
    viewModel: BidViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currency = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("da-DK")) }  // RETTET: Brug forLanguageTag – løser deprecation.

    LaunchedEffect(requestId) {
        viewModel.loadRequest(requestId)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Giv dit bud") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                state.request?.let { req ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(req.category, style = MaterialTheme.typography.titleLarge)
                            Text("${req.areaM2} m² • ${req.roomType}")
                            Text("AI-estimat: ${currency.format(req.aiPrice)}", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                OutlinedTextField(
                    value = state.price,
                    onValueChange = { viewModel.updatePrice(it) },
                    label = { Text("Din pris (kr)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.hours,
                    onValueChange = { viewModel.updateHours(it) },
                    label = { Text("Antal timer") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.materials,
                    onValueChange = { viewModel.updateMaterials(it) },
                    label = { Text("Materialer (valgfrit)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.comment,
                    onValueChange = { viewModel.updateComment(it) },
                    label = { Text("Kommentar til kunden") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.sendBid { onDismiss() } },
                enabled = state.isValid && !state.isSending
            ) {
                Text(if (state.isSending) "Sender..." else "Send bud")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuller")
            }
        }
    )
}