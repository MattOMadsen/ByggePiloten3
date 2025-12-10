package dk.byggepiloten.firma.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun SearchHistoryScreen(
    navController: NavController,
    onBack: () -> Unit = {
        navController.popBackStack("company_seeking_dashboard", inclusive = false)
        Log.d("SearchHistory", "Back kaldet – popper til dashboard")
    }
) {
    val searches = listOf(
        Search("1", "Opmuring", "10/11/2025"),
        Search("2", "Badeværelse", "09/11/2025")
    ) // Placeholder-data

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Søgehistorik") },
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
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Din søgehistorik",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            items(searches.size) { index ->
                val search = searches[index]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Søgning ${search.id}: ${search.category}", style = MaterialTheme.typography.titleMedium)
                        Text("Dato: ${search.date}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

data class Search(
    val id: String,
    val category: String,
    val date: String
)