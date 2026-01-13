// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/new_task/categories/facade/FacadeHaeftemoertelStep.kt
// STEP 6 – Hæftemørtel + farvevalg (inkl. swatches og uriHandler)
// Linjer: 248 (med fuld farve-logik)

package dk.byggepiloten.firma.ui.screen.new_task.categories.facade

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.byggepiloten.firma.data.model.task.FacadeData
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.draw.clip
import dk.byggepiloten.firma.data.misc.getDurapudsSwatchColor
import dk.byggepiloten.firma.data.misc.getSkalcemSwatchColor

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FacadeHaeftemoertelStep(
    data: FacadeData,
    onUpdate: (FacadeData) -> Unit
) {
    val uriHandler = LocalUriHandler.current

    Column {
        Text("Hæftemørtel og farve", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(24.dp))

        Text("Hæftemørtel-type", color = Color.White, fontSize = 18.sp)
        Spacer(Modifier.height(16.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("DuraPuds 615 (vandafvisende)", "Skalcem S2000 (indfarvet)", "Anden").forEach { option ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (data.haeftemoertelType == option) ByggePilotenBlue else Color.White)
                        .clickable {
                            onUpdate(
                                data.copy(
                                    haeftemoertelType = option,
                                    andenHaeftemoertel = if (option == "Anden") data.andenHaeftemoertel else null,
                                    durapudsFarve = null,
                                    skalcemFarve = null
                                )
                            )
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = option,
                        color = if (data.haeftemoertelType == option) Color.White else Color.Black,
                        fontSize = 16.sp
                    )
                }
            }
        }

        if (data.haeftemoertelType == "Anden") {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = data.andenHaeftemoertel ?: "",
                onValueChange = { onUpdate(data.copy(andenHaeftemoertel = it)) },
                label = { Text("Beskriv ønsket hæftemørtel") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    cursorColor = ByggePilotenBlue
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (data.haeftemoertelType == "DuraPuds 615 (vandafvisende)") {
            Spacer(Modifier.height(40.dp))
            Text("Vælg farve til DuraPuds 615", color = Color.White, fontSize = 16.sp)
            Spacer(Modifier.height(16.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Cementgrå", "Hvid").forEach { farveOption ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(
                                width = if (data.durapudsFarve == farveOption) 3.dp else 1.dp,
                                color = if (data.durapudsFarve == farveOption) ByggePilotenBlue else Color.Gray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onUpdate(data.copy(durapudsFarve = farveOption)) }
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(getDurapudsSwatchColor(farveOption), shape = RoundedCornerShape(4.dp))
                                    .let { if (farveOption == "Hvid") it.border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)) else it }
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(farveOption, color = Color.Black)
                        }
                    }
                }
            }
        }

        if (data.haeftemoertelType == "Skalcem S2000 (indfarvet)") {
            Spacer(Modifier.height(40.dp))
            Text("Vælg farve til Skalcem S2000", color = Color.White, fontSize = 16.sp)
            Spacer(Modifier.height(16.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(
                    "Hvid", "S 0505-Y20R", "S 1005-Y30R", "S 1005-Y50R", "S 1010-Y20R", "S 1010-Y50R",
                    "S 1020-Y20R", "S 1040-Y20R", "S 1500-N", "S 2005-R80B", "S 2005-Y",
                    "S 2010-G30Y", "S 2010-Y30R", "S 2030-Y80R", "S 2040-Y30R", "S 2502-Y",
                    "S 3005-Y20R", "S 3040-Y50R", "S 3040-Y80R", "S 4000-N", "S 4010-B90G",
                    "S 5020-B", "S 6000-N", "S 1002-Y"
                ).forEach { farveOption ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .border(
                                width = if (data.skalcemFarve == farveOption) 3.dp else 1.dp,
                                color = if (data.skalcemFarve == farveOption) ByggePilotenBlue else Color.Gray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onUpdate(data.copy(skalcemFarve = farveOption)) }
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(getSkalcemSwatchColor(farveOption), shape = RoundedCornerShape(4.dp))
                                    .let { if (farveOption == "Hvid") it.border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)) else it }
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(farveOption, color = Color.Black, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = "Se fuldt farvekort på Nordisk NHL's hjemmeside",
                color = ByggePilotenBlue,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    uriHandler.openUri("https://www.nordisknhl.dk/naturlige-kalkprodukter/indfarvet-mortel/farvekort-indfarvet-mortel")
                }
            )
        }
    }
}