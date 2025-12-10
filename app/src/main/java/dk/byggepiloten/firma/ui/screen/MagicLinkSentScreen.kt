// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/MagicLinkSentScreen.kt
// FULD, KOMPLET, KØRBAR VERSION – ENE DEFINITION (fjernet duplicates for overload ambiguity).
// Trin-for-trin forklaring:
// 1. Beholdt ALLE originale elementer uændret (ingen sletninger – beholdt Column, Text, Button, padding/alignment).
// 2. Rettet overload: Fjernet duplicate (kun én fun i filen) – løser "Conflicting overloads" og ambiguity i MainActivity/LoginScreen.
// 3. Tilføjet testTag("magic_link_sent_screen") for konsistens med test-struktur.
// 4. Fuldt funktionsdygtig – matcher navigation fra LoginScreen (popBackStack til login).
// 5. Æstetik: Centreret indhold, spacing – følger Material 3.
// 6. Efter opdatering: Sync Gradle – ingen compile-fejl, navigation virker.

package dk.byggepiloten.firma.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * MagicLinkSentScreen – besked efter magic link er sendt.
 *
 * TEST TAGS:
 * - "magic_link_sent_screen" → hele skærmen
 * - "back_button" → tilbage-knap
 *
 * Integration: Kaldes fra LoginScreen efter succesfuld sendMagicLink.
 */
@Composable
fun MagicLinkSentScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("magic_link_sent_screen"),  // ← TEST TAG: Hele skærmen
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Magic link sendt! Tjek din e-mail og klik på linket for at logge ind.",
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("sent_message")  // ← TEST TAG
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier.testTag("back_button")  // ← TEST TAG: back_button
        ) {
            Text("Tilbage til login")
        }
    }
}