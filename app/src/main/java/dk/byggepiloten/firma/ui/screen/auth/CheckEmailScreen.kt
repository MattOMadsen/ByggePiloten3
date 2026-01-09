// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/CheckEmailScreen.kt
// Forklaring trin-for-trin: Beholdt DIN ORIGINALE KODE 100% UÆNDRET (ingen sletninger – beholdt Column, Text, Button).
// NYT TILFØJET: Ingenting – filen er allerede god; brugt i navigation fra ContractorDetailsScreen.
// Fuldt funktionsdygtig – ingen ændringer nødvendige.

package dk.byggepiloten.firma.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CheckEmailScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Tjek din e-mail!", style = MaterialTheme.typography.headlineMedium)
        Text("Klik på linket i mailen for at logge ind.")
        Spacer(Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) { Text("Tilbage") }
    }
}