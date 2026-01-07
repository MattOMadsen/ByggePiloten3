// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/FacadePudsningScreen.kt
// FULD FIL – OPdateret version (kompilerer 100% i Material3)
// Ændringer:
// - Rettet fejl: Helper-funktionerne flyttet udenfor @Composable og gjort til top-level private fun
// - Fjernet separat "Farve (slutpuds)"
// - Farvevalg kun for valgt hæftemørtel-type (DuraPuds eller Skalcem)
// - Swatch viser approximate farver

package dk.byggepiloten.firma.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FacadePudsningScreen(navController: NavController) {
    var area by remember { mutableStateOf("") }
    var vaegtype by remember { mutableStateOf<String?>(null) }
    var andenVaegtype by remember { mutableStateOf("") }
    var hojde by remember { mutableStateOf("") }

    var stilladsNoedvendigt by remember { mutableStateOf<String?>(null) }
    var stilladsAdgang by remember { mutableStateOf<String?>(null) }
    var stilladsTrapper by remember { mutableStateOf<String?>(null) }

    var underlagRevner by remember { mutableStateOf<String?>(null) }
    var underlagFugt by remember { mutableStateOf<String?>(null) }
    var underlagGammelPuds by remember { mutableStateOf<String?>(null) }

    var vejretidspunkt by remember { mutableStateOf<String?>(null) }
    var vandskur by remember { mutableStateOf<String?>(null) }
    var haeftemoertelType by remember { mutableStateOf<String?>(null) }
    var durapudsFarve by remember { mutableStateOf<String?>(null) }
    var skalcemFarve by remember { mutableStateOf<String?>(null) }

    var currentStep by remember { mutableStateOf(1) }
    val totalSteps = 6

    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        disabledContainerColor = Color.White.copy(alpha = 0.6f),
        errorContainerColor = Color.White,
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        disabledTextColor = Color.Black.copy(alpha = 0.5f),
        errorTextColor = Color.Red,
        cursorColor = ByggePilotenBlue,
        errorCursorColor = Color.Red,
        focusedIndicatorColor = ByggePilotenBlue,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Gray,
        errorIndicatorColor = Color.Red,
        focusedLabelColor = Color.Transparent,
        unfocusedLabelColor = Color.Transparent,
        disabledLabelColor = Color.Transparent,
        errorLabelColor = Color.Transparent,
        focusedPlaceholderColor = Color.Gray,
        unfocusedPlaceholderColor = Color.Gray.copy(alpha = 0.6f),
        disabledPlaceholderColor = Color.Gray.copy(alpha = 0.4f),
        errorPlaceholderColor = Color.Gray
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(ByggePilotenBlue, Color(0xFF42A5F5), Color(0xFF90CAF9))
                )
            )
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Facadepudsning", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tilbage", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ByggePilotenBlue)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                LinearProgressIndicator(
                    progress = { currentStep / totalSteps.toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
                Spacer(Modifier.height(8.dp))
                Text("Step $currentStep af $totalSteps", color = Color.White, fontSize = 16.sp)

                Spacer(Modifier.height(32.dp))

                when (currentStep) {
                    1 -> {
                        Text("Grundlæggende info", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 18.sp)
                        Spacer(Modifier.height(24.dp))

                        Text("Areal der skal pudses (m²)", color = Color.White, fontSize = 16.sp)
                        OutlinedTextField(
                            value = area,
                            onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) area = it },
                            placeholder = { Text("Indtast areal i m²") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors
                        )

                        Spacer(Modifier.height(32.dp))

                        Text("Vægtype (påvirker mørtel)", color = Color.White, fontSize = 16.sp)
                        val vaegtyper = listOf("Mursten", "Puds", "Træ", "Lega", "Beton", "Anden")
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            vaegtyper.forEach { type ->
                                Box(
                                    modifier = Modifier
                                        .clickable {
                                            vaegtype = type
                                            if (type != "Anden") andenVaegtype = ""
                                        }
                                        .background(
                                            if (vaegtype == type) ByggePilotenBlue else Color.White,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = type,
                                        color = if (vaegtype == type) Color.White else Color.Black,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        if (vaegtype == "Anden") {
                            Spacer(Modifier.height(16.dp))
                            OutlinedTextField(
                                value = andenVaegtype,
                                onValueChange = { andenVaegtype = it },
                                placeholder = { Text("Beskriv vægtype") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors
                            )
                        }

                        Spacer(Modifier.height(32.dp))

                        Text("Højde op til højeste punkt (m)", color = Color.White, fontSize = 16.sp)
                        OutlinedTextField(
                            value = hojde,
                            onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) hojde = it },
                            placeholder = { Text("Indtast højde i meter") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors
                        )
                    }
                    2 -> {
                        Text("Adgang og stillads", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 18.sp)
                        Spacer(Modifier.height(24.dp))

                        Text("Er stillads nødvendigt?", color = Color.White, fontSize = 16.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .clickable { stilladsNoedvendigt = "Ja" }
                                    .background(
                                        if (stilladsNoedvendigt == "Ja") ByggePilotenBlue else Color.White,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 24.dp, vertical = 12.dp)
                            ) {
                                Text("Ja", color = if (stilladsNoedvendigt == "Ja") Color.White else Color.Black)
                            }
                            Box(
                                modifier = Modifier
                                    .clickable { stilladsNoedvendigt = "Nej" }
                                    .background(
                                        if (stilladsNoedvendigt == "Nej") ByggePilotenBlue else Color.White,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 24.dp, vertical = 12.dp)
                            ) {
                                Text("Nej", color = if (stilladsNoedvendigt == "Nej") Color.White else Color.Black)
                            }
                        }

                        if (stilladsNoedvendigt == "Ja") {
                            Spacer(Modifier.height(32.dp))
                            Text("Er der adgang til at få stillads frem?", color = Color.White, fontSize = 16.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clickable { stilladsAdgang = "Ja" }
                                        .background(
                                            if (stilladsAdgang == "Ja") ByggePilotenBlue else Color.White,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 24.dp, vertical = 12.dp)
                                ) {
                                    Text("Ja", color = if (stilladsAdgang == "Ja") Color.White else Color.Black)
                                }
                                Box(
                                    modifier = Modifier
                                        .clickable { stilladsAdgang = "Nej" }
                                        .background(
                                            if (stilladsAdgang == "Nej") ByggePilotenBlue else Color.White,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 24.dp, vertical = 12.dp)
                                ) {
                                    Text("Nej", color = if (stilladsAdgang == "Nej") Color.White else Color.Black)
                                }
                            }

                            Spacer(Modifier.height(32.dp))
                            Text("Skal stillads bæres op ad trapper?", color = Color.White, fontSize = 16.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clickable { stilladsTrapper = "Ja" }
                                        .background(
                                            if (stilladsTrapper == "Ja") ByggePilotenBlue else Color.White,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 24.dp, vertical = 12.dp)
                                ) {
                                    Text("Ja", color = if (stilladsTrapper == "Ja") Color.White else Color.Black)
                                }
                                Box(
                                    modifier = Modifier
                                        .clickable { stilladsTrapper = "Nej" }
                                        .background(
                                            if (stilladsTrapper == "Nej") ByggePilotenBlue else Color.White,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 24.dp, vertical = 12.dp)
                                ) {
                                    Text("Nej", color = if (stilladsTrapper == "Nej") Color.White else Color.Black)
                                }
                            }
                        }
                    }
                    3 -> {
                        Text("Underlagstilstand", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 18.sp)
                        Spacer(Modifier.height(24.dp))

                        val underlagQuestions = listOf(
                            Triple("Revner i underlag", underlagRevner) { value: String -> underlagRevner = value },
                            Triple("Fugt i underlag", underlagFugt) { value: String -> underlagFugt = value },
                            Triple("Gammel puds skal fjernes", underlagGammelPuds) { value: String -> underlagGammelPuds = value }
                        )

                        underlagQuestions.forEach { (label, state, setter) ->
                            Text(label, color = Color.White, fontSize = 16.sp)
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clickable { setter("Ja") }
                                        .background(
                                            if (state == "Ja") ByggePilotenBlue else Color.White,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 24.dp, vertical = 12.dp)
                                ) {
                                    Text("Ja", color = if (state == "Ja") Color.White else Color.Black)
                                }
                                Box(
                                    modifier = Modifier
                                        .clickable { setter("Nej") }
                                        .background(
                                            if (state == "Nej") ByggePilotenBlue else Color.White,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 24.dp, vertical = 12.dp)
                                ) {
                                    Text("Nej", color = if (state == "Nej") Color.White else Color.Black)
                                }
                            }
                            Spacer(Modifier.height(32.dp))
                        }
                    }
                    4 -> {
                        Text("Vejrforhold", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 18.sp)
                        Spacer(Modifier.height(24.dp))

                        Text("Vejretidspunkt (frostpåvirkning)", color = Color.White, fontSize = 16.sp)
                        Spacer(Modifier.height(16.dp))
                        val vejrOptions = listOf("Sommer", "Vinter", "Forår/Efterår")
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            vejrOptions.forEach { option ->
                                Box(
                                    modifier = Modifier
                                        .clickable { vejretidspunkt = option }
                                        .background(
                                            if (vejretidspunkt == option) ByggePilotenBlue else Color.White,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = option,
                                        color = if (vejretidspunkt == option) Color.White else Color.Black,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                    5 -> {
                        Text("Øvrige detaljer", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 18.sp)
                        Spacer(Modifier.height(24.dp))

                        Text("Vandskur (tyndpudsning/filtsning – vandafvisende)", color = Color.White, fontSize = 16.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .clickable { vandskur = "Ja" }
                                    .background(
                                        if (vandskur == "Ja") ByggePilotenBlue else Color.White,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 24.dp, vertical = 12.dp)
                            ) {
                                Text("Ja", color = if (vandskur == "Ja") Color.White else Color.Black)
                            }
                            Box(
                                modifier = Modifier
                                    .clickable { vandskur = "Nej" }
                                    .background(
                                        if (vandskur == "Nej") ByggePilotenBlue else Color.White,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 24.dp, vertical = 12.dp)
                            ) {
                                Text("Nej", color = if (vandskur == "Nej") Color.White else Color.Black)
                            }
                        }

                        Spacer(Modifier.height(40.dp))
                        Text("Hæftemørtel-type", color = Color.White, fontSize = 16.sp)
                        Spacer(Modifier.height(16.dp))
                        val haefteOptions = listOf("DuraPuds 615 (vandafvisende)", "Skalcem S2000 (indfarvet)", "Anden")
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            haefteOptions.forEach { option ->
                                Box(
                                    modifier = Modifier
                                        .clickable {
                                            haeftemoertelType = option
                                            durapudsFarve = null
                                            skalcemFarve = null
                                        }
                                        .background(
                                            if (haeftemoertelType == option) ByggePilotenBlue else Color.White,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = option,
                                        color = if (haeftemoertelType == option) Color.White else Color.Black,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        if (haeftemoertelType == "DuraPuds 615 (vandafvisende)") {
                            Spacer(Modifier.height(40.dp))
                            Text("Vælg farve til DuraPuds 615", color = Color.White, fontSize = 16.sp)
                            Spacer(Modifier.height(16.dp))
                            val durapudsFarver = listOf("Cementgrå", "Hvid")
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                durapudsFarver.forEach { farveOption ->
                                    Box(
                                        modifier = Modifier
                                            .clickable { durapudsFarve = farveOption }
                                            .border(
                                                width = if (durapudsFarve == farveOption) 3.dp else 1.dp,
                                                color = if (durapudsFarve == farveOption) ByggePilotenBlue else Color.Gray,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                                            .padding(8.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .background(getDurapudsSwatchColor(farveOption), shape = RoundedCornerShape(4.dp))
                                            )
                                            Spacer(Modifier.width(12.dp))
                                            Text(
                                                text = farveOption,
                                                color = Color.Black,
                                                fontSize = 16.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (haeftemoertelType == "Skalcem S2000 (indfarvet)") {
                            Spacer(Modifier.height(40.dp))
                            Text("Vælg farve til Skalcem S2000", color = Color.White, fontSize = 16.sp)
                            Spacer(Modifier.height(16.dp))
                            val skalcemFarver = listOf(
                                "Hvid",
                                "S 0505-Y20R", "S 1005-Y30R", "S 1005-Y50R", "S 1010-Y20R", "S 1010-Y50R",
                                "S 1020-Y20R", "S 1040-Y20R", "S 1500-N", "S 2005-R80B", "S 2005-Y",
                                "S 2010-G30Y", "S 2010-Y30R", "S 2030-Y80R", "S 2040-Y30R", "S 2502-Y",
                                "S 3005-Y20R", "S 3040-Y50R", "S 3040-Y80R", "S 4000-N", "S 4010-B90G",
                                "S 5020-B", "S 6000-N", "S 1002-Y"
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                skalcemFarver.forEach { farveOption ->
                                    Box(
                                        modifier = Modifier
                                            .clickable { skalcemFarve = farveOption }
                                            .border(
                                                width = if (skalcemFarve == farveOption) 3.dp else 1.dp,
                                                color = if (skalcemFarve == farveOption) ByggePilotenBlue else Color.Gray,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                                            .padding(8.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .background(getSkalcemSwatchColor(farveOption), shape = RoundedCornerShape(4.dp))
                                            )
                                            Spacer(Modifier.width(12.dp))
                                            Text(
                                                text = farveOption,
                                                color = Color.Black,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    6 -> {
                        Text("Opsummering", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(Modifier.height(24.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("Areal: $area m²", color = Color.Black, fontSize = 16.sp)
                                Text("Vægtype: ${if (vaegtype == "Anden") "$vaegtype ($andenVaegtype)" else vaegtype ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                                Text("Højde: $hojde m", color = Color.Black, fontSize = 16.sp)
                                Text("Stillads nødvendigt: ${stilladsNoedvendigt ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                                if (stilladsNoedvendigt == "Ja") {
                                    Text("Adgang til stillads: ${stilladsAdgang ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                                    Text("Bæres op ad trapper: ${stilladsTrapper ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                                }
                                Text("Revner i underlag: ${underlagRevner ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                                Text("Fugt i underlag: ${underlagFugt ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                                Text("Gammel puds fjernes: ${underlagGammelPuds ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                                Text("Vejretidspunkt: ${vejretidspunkt ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                                Text("Vandskur: ${vandskur ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                                Text("Hæftemørtel-type: ${haeftemoertelType ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                                if (haeftemoertelType == "DuraPuds 615 (vandafvisende)") {
                                    Text("DuraPuds farve: ${durapudsFarve ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                                }
                                if (haeftemoertelType == "Skalcem S2000 (indfarvet)") {
                                    Text("Skalcem farve: ${skalcemFarve ?: "Ikke valgt"}", color = Color.Black, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(40.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (currentStep > 1) {
                        Button(
                            onClick = { currentStep-- },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text("Tilbage", color = ByggePilotenBlue)
                        }
                    } else {
                        Spacer(Modifier.width(1.dp))
                    }

                    Button(
                        onClick = {
                            if (currentStep < totalSteps) {
                                if (currentStep == 1 && area.isBlank()) return@Button
                                currentStep++
                            } else {
                                Timber.d("Navigated to task_photos_description/facade_pudsning")
                                navController.navigate("task_photos_description/facade_pudsning")
                            }
                        },
                        enabled = if (currentStep == 1) area.isNotBlank() else true,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(if (currentStep < totalSteps) "Næste" else "Fortsæt til billeder", color = ByggePilotenBlue)
                    }
                }
            }
        }
    }
}

private fun getDurapudsSwatchColor(farve: String): Color {
    return when (farve) {
        "Cementgrå" -> Color(0xFFAAAAAA)
        "Hvid" -> Color(0xFFFFFFFF)
        else -> Color(0xFFD0D0D0)
    }
}

private fun getSkalcemSwatchColor(ncsCode: String): Color {
    return when (ncsCode) {
        "Hvid" -> Color(0xFFFFFFFF)
        "S 0505-Y20R" -> Color(0xFFF5E8E0)
        "S 1005-Y30R" -> Color(0xFFF2E0D8)
        "S 1005-Y50R" -> Color(0xFFF0D8D0)
        "S 1010-Y20R" -> Color(0xFFF0E0D0)
        "S 1010-Y50R" -> Color(0xFFE8D0C8)
        "S 1020-Y20R" -> Color(0xFFF0D8C0)
        "S 1040-Y20R" -> Color(0xFFF0C890)
        "S 1500-N" -> Color(0xFFD8D8D8)
        "S 2005-R80B" -> Color(0xFFD0D8E8)
        "S 2005-Y" -> Color(0xFFF0F0E0)
        "S 2010-G30Y" -> Color(0xFFC8E0D0)
        "S 2010-Y30R" -> Color(0xFFE8D8C8)
        "S 2030-Y80R" -> Color(0xFFE0C0B8)
        "S 2040-Y30R" -> Color(0xFFF0B080)
        "S 2502-Y" -> Color(0xFFE0E0D8)
        "S 3005-Y20R" -> Color(0xFFE0D8D0)
        "S 3040-Y50R" -> Color(0xFFE8A870)
        "S 3040-Y80R" -> Color(0xFFD09080)
        "S 4000-N" -> Color(0xFFA8A8A8)
        "S 4010-B90G" -> Color(0xFFA0B8B0)
        "S 5020-B" -> Color(0xFF607080)
        "S 6000-N" -> Color(0xFF909090)
        "S 1002-Y" -> Color(0xFFF0F0E8)
        else -> Color(0xFFD0D0D0)
    }
}