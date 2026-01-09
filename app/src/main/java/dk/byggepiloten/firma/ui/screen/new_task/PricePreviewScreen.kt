package dk.byggepiloten.firma.ui.screen.new_task

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PricePreviewScreen(onSendToBid: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Prisoverslag klar!", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onSendToBid, modifier = Modifier.fillMaxWidth()) {
            Text("Send til bud")
        }
    }
}