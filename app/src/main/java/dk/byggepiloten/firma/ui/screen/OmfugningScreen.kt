// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/OmfugningScreen.kt
// 100% KOMPILERBAR – FIXET VERSION (kun én ændring!)

package dk.byggepiloten.firma.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class Wall(
    val id: Int = 0,
    var length: String = "",
    var height: String = "",
    var isGavl: Boolean = false,
    var windowsDoors: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OmfugningScreen(navController: NavController) {
    var walls by remember { mutableStateOf(listOf(Wall(1))) }
    var nextId by remember { mutableStateOf(2) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Omfugning") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Tilbage")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            items(walls) { wall ->
                WallInputCard(
                    wall = wall,
                    totalWalls = walls.size,  // Vi sender antal vægge med
                    onUpdate = { updated ->
                        walls = walls.map { if (it.id == wall.id) updated else it }
                    },
                    onRemove = {
                        walls = walls.filter { it.id != wall.id }
                    }
                )
                Spacer(Modifier.height(16.dp))
            }

            item {
                Button(
                    onClick = {
                        walls = walls + Wall(nextId++)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("+ TILFØJ VÆG")
                }
            }

            item {
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = { navController.navigate("task_photos_description") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = walls.any { it.length.isNotBlank() && it.height.isNotBlank() }
                ) {
                    Text("Fortsæt til billeder")
                }
            }
        }
    }
}

@Composable
private fun WallInputCard(
    wall: Wall,
    totalWalls: Int,  // Nyt parameter!
    onUpdate: (Wall) -> Unit,
    onRemove: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                OutlinedTextField(
                    value = wall.length,
                    onValueChange = { onUpdate(wall.copy(length = it)) },
                    label = { Text("Længde (m)") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = wall.height,
                    onValueChange = { onUpdate(wall.copy(height = it)) },
                    label = { Text("Højde (m)") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = wall.isGavl,
                    onCheckedChange = { onUpdate(wall.copy(isGavl = it)) }
                )
                Text("Er væggen en gavl")
            }
            Spacer(Modifier.height(8.dp))
            Row {
                Button(onClick = { onUpdate(wall.copy(windowsDoors = wall.windowsDoors + 1)) }) {
                    Text("TILFØJ VINDUE/DØR")
                }
                Spacer(Modifier.width(8.dp))
                if (wall.windowsDoors > 0) {
                    Text("${wall.windowsDoors} tilføjet")
                }
            }
            Spacer(Modifier.height(8.dp))

            // KUN vis "Fjern" hvis der er mere end én væg
            if (totalWalls > 1) {
                Button(
                    onClick = onRemove,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("FJERN VÆG")
                }
            }
        }
    }
}