// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/facade/FacadeOpsummeringStep.kt
// STEP 7 – Opsummering (card med alle værdier)
// Linjer: 168

package dk.byggepiloten.firma.ui.screen.new_task.categories.facade

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dk.byggepiloten.firma.data.model.task.FacadeData

@Composable
fun FacadeOpsummeringStep(
    data: FacadeData,
    navController: NavController
) {
    Column {
        Text("Opsummering", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Areal: ${data.area?.toString() ?: "Ikke angivet"} m²", color = Color.Black, fontSize = 16.sp)
                Text("Vægtype: ${if (data.vaegtype == "Anden") "Anden (${data.andenVaegtype ?: ""})" else data.vaegtype ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                Text("Højde: ${data.hojde?.toString() ?: "Ikke angivet"} m", color = Color.Black, fontSize = 16.sp)
                Text("Stillads nødvendigt: ${data.stilladsNoedvendigt ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                if (data.stilladsNoedvendigt == "Ja") {
                    Text("Adgang til stillads: ${data.stilladsAdgang ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                    Text("Bæres op ad trapper: ${data.stilladsTrapper ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                }
                Text("Armeringsnet: ${data.armeringsnet ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                Text("Facadeisolering: ${data.isolering ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                if (data.isolering == "Ja") {
                    Text("Isoleringstype: ${data.isoleringType ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                }
                Text("Revner i underlag: ${data.underlagRevner ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                Text("Fugt i underlag: ${data.underlagFugt ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                Text("Gammel puds fjernes: ${data.underlagGammelPuds ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                Text("Vejretidspunkt: ${data.vejretidspunkt ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                Text("Hæftemørtel-type: ${if (data.haeftemoertelType == "Anden") "Anden (${data.andenHaeftemoertel ?: ""})" else data.haeftemoertelType ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                if (data.haeftemoertelType == "DuraPuds 615 (vandafvisende)") {
                    Text("DuraPuds farve: ${data.durapudsFarve ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                }
                if (data.haeftemoertelType == "Skalcem S2000 (indfarvet)") {
                    Text("Skalcem farve: ${data.skalcemFarve ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                }
            }
        }
    }
}