package dk.byggepiloten.firma.ui.screen.dashboard

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractorDashboardScreen(
    navController: NavController, // FIXED: Fjernet valgfri ? for at sikre navigation
    onBack: () -> Unit = {
        navController.popBackStack("role_selection", inclusive = false)
        Log.d("ContractorDashboard", "Back kaldet – popper til rolle")
    }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Entreprenør dashboard") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Tilbage"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Velkommen til entreprenør dashboard",
                style = MaterialTheme.typography.headlineMedium
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Aktive opgaver", style = MaterialTheme.typography.labelLarge)
                    Text("3 nye opgaver tilgængelige!")
                }
            }
            Button(
                onClick = {
                    Log.d("ContractorDashboard", "Se nye opgaver klikket – navigerer til bid_pool")
                    navController.navigate("bid_pool")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { // FIXED: Tilføjet clickable for ekstra feedback
                        Log.d("ContractorDashboard", "Se nye opgaver clickable trigget")
                        navController.navigate("bid_pool")
                    }
            ) {
                Text("Se nye opgaver")
            }
            Button(
                onClick = {
                    Log.d("ContractorDashboard", "Mine tilbud klikket – navigerer til mine_tilbud")
                    navController.navigate("mine_tilbud")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { // FIXED: Tilføjet clickable for ekstra feedback
                        Log.d("ContractorDashboard", "Mine tilbud clickable trigget")
                        navController.navigate("mine_tilbud")
                    }
            ) {
                Text("Mine tilbud")
            }
        }
    }
}