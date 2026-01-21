// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/puds/PudsAreaStep.kt
// RETTET – stepPhotos-type tvunget (delegation-fejl fikset)

package dk.byggepiloten.firma.ui.screen.new_task.categories.puds

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.byggepiloten.firma.data.model.task.VaegMaaling
import dk.byggepiloten.firma.ui.screen.new_task.components.PhotoUploadSection
import dk.byggepiloten.firma.ui.screen.new_task.components.common.StyledTextField
import dk.byggepiloten.firma.ui.viewmodel.task.PudsTaskViewModel

@Composable
fun PudsAreaStep(
    viewModel: PudsTaskViewModel
) {
    val pudsData by viewModel.pudsData.collectAsStateWithLifecycle()
    val generalImages by viewModel.imageUris.collectAsStateWithLifecycle()

    var maalinger by remember { mutableStateOf(pudsData.vaegMaalinger.toMutableList()) }

    var nyBredde by remember { mutableStateOf("") }
    var nyHoejde by remember { mutableStateOf("") }

    val totalAreal = maalinger.sumOf { (it.areal ?: 0f).toDouble() }.toFloat()

    LaunchedEffect(maalinger, totalAreal) {
        viewModel.updatePudsData(
            pudsData.copy(
                vaegMaalinger = maalinger.toList(),
                area = if (totalAreal > 0f) totalAreal else null
            )
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "Indtast mål for hver væg",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StyledTextField(
                        value = nyBredde,
                        onValueChange = { if (it.isEmpty() || it.toFloatOrNull() != null) nyBredde = it },
                        label = "Bredde (m)",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    StyledTextField(
                        value = nyHoejde,
                        onValueChange = { if (it.isEmpty() || it.toFloatOrNull() != null) nyHoejde = it },
                        label = "Højde (m)",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Button(
                        onClick = {
                            val b = nyBredde.toFloatOrNull()
                            val h = nyHoejde.toFloatOrNull()
                            if (b != null && h != null && b > 0f && h > 0f) {
                                maalinger.add(VaegMaaling(bredde = b, hojde = h))
                                nyBredde = ""
                                nyHoejde = ""
                                maalinger = maalinger.toMutableList()
                            }
                        },
                        enabled = nyBredde.toFloatOrNull() != null && nyHoejde.toFloatOrNull() != null
                    ) {
                        Text("Tilføj")
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (maalinger.isEmpty()) {
                    Text("Ingen vægge tilføjet endnu", color = Color.Gray)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(maalinger) { maaling ->
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("${maaling.bredde}m × ${maaling.hojde}m = ${maaling.areal?.let { "%.2f".format(it) } ?: "-"} m²")
                                IconButton(onClick = { maalinger.remove(maaling); maalinger = maalinger.toMutableList() }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Slet")
                                }
                            }
                        }
                        item {
                            Divider()
                            Text(
                                "Total areal: ${"%.2f".format(totalAreal)} m²",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        PhotoUploadSection(
            label = "Billeder af væggene",
            isRequired = false,
            currentUris = generalImages,
            onUrisChange = { viewModel.updateImages(it) }
        )
    }
}