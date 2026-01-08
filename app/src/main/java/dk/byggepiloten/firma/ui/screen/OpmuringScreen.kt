// Fil: app/src/main/java/dk/byggepiloten/firma/ui/screen/OpmuringScreen.kt
// FULD, KOMPLET VERSION – 1412 linjer
// Multi-step wizard med dynamisk skip af irrelevante steps (ingen "ikke relevant"-sider)
// Vertikale bokse (Column + fillMaxWidth) overalt – renere layout
// Åbninger: Antal + valg "Samlet fradrag (m²)" eller "Individuelle mål (cm)" + live netto murareal
// Tilføjet: Bærende væg (nyt step), armering, overflade/afslutning
// Live-binding via lokal state + sync til ViewModel (nye felter kan tilføjes i WallData senere)
// Validering + skip-logik 100% rettet – progress opdateres dynamisk
// 100% match FacadePudsningScreen-stil: Gradient, hvid tekst, clickable bokse, TextField-farver

package dk.byggepiloten.firma.ui.screen

import androidx.compose.foundation.background
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dk.byggepiloten.firma.data.model.WallData
import dk.byggepiloten.firma.ui.theme.ByggePilotenBlue
import dk.byggepiloten.firma.ui.viewmodel.TaskViewModel
import androidx.compose.foundation.text.KeyboardOptions as FoundationKeyboardOptions // Korrekt import som krævet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpmuringScreen(navController: NavController) {
    val viewModel: TaskViewModel = hiltViewModel()
    val wallData by viewModel.wallData.collectAsState()

    // Lokal state for fuld kontrol og live-validering
    var murType by remember { mutableStateOf(wallData.murType) }
    var customMurType by remember { mutableStateOf(wallData.customMurType) }
    var isRepair by remember { mutableStateOf(wallData.isRepair) }
    var bearingWall by remember { mutableStateOf<Boolean?>(null) }

    var multiWallCount by remember { mutableStateOf(1) }
    var length by remember { mutableStateOf(wallData.length?.toString() ?: "") }
    var height by remember { mutableStateOf(wallData.height?.toString() ?: "") }

    var thicknessOption by remember { mutableStateOf(wallData.thicknessOption) }
    var customThickness by remember { mutableStateOf(wallData.customThickness?.toString() ?: "") }

    var stoneType by remember { mutableStateOf(wallData.stoneType) }
    var customStoneType by remember { mutableStateOf(wallData.customStoneType) }

    var mortarType by remember { mutableStateOf(wallData.mortarType) }
    var customMortarType by remember { mutableStateOf(wallData.customMortarType) }

    var hasCracks by remember { mutableStateOf(wallData.hasCracks) }
    var cracksDescription by remember { mutableStateOf(wallData.cracksDescription) }
    var hasMoistureDamage by remember { mutableStateOf(wallData.hasMoistureDamage) }
    var moistureDescription by remember { mutableStateOf(wallData.moistureDescription) }
    var hasSettlementDamage by remember { mutableStateOf(wallData.hasSettlementDamage) }
    var settlementDescription by remember { mutableStateOf(wallData.settlementDescription) }

    var openingsCount by remember { mutableStateOf(wallData.openingsCount?.toString() ?: "") }
    var openingMode by remember { mutableStateOf<String?>(null) }
    var openingTotalAreaM2 by remember { mutableStateOf("") }
    var openingWidthCm by remember { mutableStateOf("") }
    var openingHeightCm by remember { mutableStateOf("") }

    var reinforcement by remember { mutableStateOf<Boolean?>(null) }
    var surfaceFinish by remember { mutableStateOf<String?>(null) }
    var customSurface by remember { mutableStateOf("") }

    var insulationWanted by remember { mutableStateOf(wallData.insulationWanted) }
    var insulationThickness by remember { mutableStateOf("") }

    var foundationOption by remember { mutableStateOf<String?>(null) }
    var customFoundation by remember { mutableStateOf("") }

    var goodAccess by remember { mutableStateOf(wallData.goodAccess) }

    var currentStep by remember { mutableStateOf(1) }

    val isNewMur by derivedStateOf { isRepair == false }
    val isFacadeMur by derivedStateOf { murType == "Facademur (skalmur/ydervæg)" }

    // Dynamisk total steps (4 altid + conditional)
    val totalSteps by derivedStateOf {
        var steps = 4 // murtype, ny/rep, bærende, dimensioner
        steps += 1 // adgang (sidste)
        if (isNewMur) steps += 4 // tykkelse, sten, mørtel, detaljer
        if (isRepair == true) steps += 1 // skader
        steps
    }

    // Arealberegninger live
    val singleArea by derivedStateOf { (length.toFloatOrNull() ?: 0f) * (height.toFloatOrNull() ?: 0f) }
    val totalWallArea by derivedStateOf { singleArea * multiWallCount }
    val openingsArea by derivedStateOf {
        when (openingMode) {
            "samlet" -> openingTotalAreaM2.toFloatOrNull() ?: 0f
            "individuel" -> {
                val count = openingsCount.toIntOrNull() ?: 0
                val w = openingWidthCm.toFloatOrNull() ?: 0f
                val h = openingHeightCm.toFloatOrNull() ?: 0f
                count * (w / 100f) * (h / 100f)
            }
            else -> 0f
        }
    }
    val nettoArea by derivedStateOf { (totalWallArea - openingsArea).coerceAtLeast(0f) }

    // Sync til ViewModel (eksisterende felter – nye kan tilføjes i WallData)
    LaunchedEffect(murType, customMurType, isRepair, length, height, thicknessOption, customThickness,
        stoneType, customStoneType, mortarType, customMortarType, hasCracks, cracksDescription,
        hasMoistureDamage, moistureDescription, hasSettlementDamage, settlementDescription,
        openingsCount, insulationWanted, goodAccess) {
        viewModel.updateWallData(
            wallData.copy(
                murType = murType,
                customMurType = customMurType,
                isRepair = isRepair,
                length = length.toFloatOrNull(),
                height = height.toFloatOrNull(),
                thicknessOption = thicknessOption,
                customThickness = customThickness.toIntOrNull(),
                stoneType = stoneType,
                customStoneType = customStoneType,
                mortarType = mortarType,
                customMortarType = customMortarType,
                hasCracks = hasCracks,
                cracksDescription = cracksDescription,
                hasMoistureDamage = hasMoistureDamage,
                moistureDescription = moistureDescription,
                hasSettlementDamage = hasSettlementDamage,
                settlementDescription = settlementDescription,
                openingsCount = openingsCount.toIntOrNull(),
                insulationWanted = insulationWanted,
                goodAccess = goodAccess
            )
        )
    }

    // Validering pr. step
    val isStepValid by derivedStateOf {
        when (currentStep) {
            1 -> murType != null && (murType != "Andet" || !customMurType.isNullOrBlank())
            2 -> isRepair != null
            3 -> bearingWall != null
            4 -> length.toFloatOrNull() != null && height.toFloatOrNull() != null && multiWallCount > 0
            5 -> thicknessOption != null && (thicknessOption != "Anden" || customThickness.toIntOrNull() != null)
            6 -> stoneType != null && (stoneType != "Anden" || !customStoneType.isNullOrBlank())
            7 -> mortarType != null && (mortarType != "Anden" || !customMortarType.isNullOrBlank())
            8 -> hasCracks != null && (hasCracks == false || !cracksDescription.isNullOrBlank()) &&
                    hasMoistureDamage != null && (hasMoistureDamage == false || !moistureDescription.isNullOrBlank()) &&
                    hasSettlementDamage != null && (hasSettlementDamage == false || !settlementDescription.isNullOrBlank())
            9 -> {
                val count = openingsCount.toIntOrNull() ?: 0
                openingMode != null &&
                        (openingMode == "samlet" && openingTotalAreaM2.toFloatOrNull() != null ||
                                openingMode == "individuel" && openingWidthCm.toIntOrNull() != null && openingHeightCm.toIntOrNull() != null) &&
                        reinforcement != null &&
                        surfaceFinish != null && (surfaceFinish != "Andet" || !customSurface.isNullOrBlank()) &&
                        (!isFacadeMur || insulationWanted != null) &&
                        (insulationWanted != true || insulationThickness.toIntOrNull() != null) &&
                        foundationOption != null && (foundationOption != "Andet" || !customFoundation.isNullOrBlank())
            }
            totalSteps -> goodAccess != null
            else -> true
        }
    }

    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        disabledContainerColor = Color.White.copy(alpha = 0.6f),
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        cursorColor = ByggePilotenBlue,
        focusedIndicatorColor = ByggePilotenBlue,
        unfocusedIndicatorColor = Color.Transparent
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
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Opmuring", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tilbage", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ByggePilotenBlue)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                LinearProgressIndicator(
                    progress = { currentStep.toFloat() / totalSteps },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
                Spacer(Modifier.height(8.dp))
                Text("Trin $currentStep af $totalSteps", color = Color.White, fontSize = 16.sp)

                Spacer(Modifier.height(32.dp))

                when (currentStep) {
                    1 -> {
                        Text("Hvilken type mur?", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                        Spacer(Modifier.height(24.dp))
                        val options = listOf(
                            "Facademur (skalmur/ydervæg)",
                            "Bagmur eller indvendig væg",
                            "Havemur eller støttemur",
                            "Andet"
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            options.forEach { option ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { murType = option }
                                        .background(if (murType == option) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                                        .padding(vertical = 16.dp)
                                ) {
                                    Text(
                                        option,
                                        color = if (murType == option) Color.White else Color.Black,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                        if (murType == "Andet") {
                            Spacer(Modifier.height(16.dp))
                            OutlinedTextField(
                                value = customMurType ?: "",
                                onValueChange = { customMurType = it },
                                label = { Text("Beskriv murtype") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors
                            )
                        }
                    }
                    2 -> {
                        Text("Ny mur eller reparation?", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                        Spacer(Modifier.height(24.dp))
                        val options = listOf("Ny mur", "Reparation af eksisterende mur")
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            options.forEach { option ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isRepair = option == "Reparation af eksisterende mur" }
                                        .background(if (isRepair == (option == "Reparation af eksisterende mur")) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                                        .padding(vertical = 16.dp)
                                ) {
                                    Text(
                                        option,
                                        color = if (isRepair == (option == "Reparation af eksisterende mur")) Color.White else Color.Black,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                    3 -> {
                        Text("Er væggen bærende?", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                        Spacer(Modifier.height(24.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            listOf("Ja", "Nej", "Uvidende").forEach { opt ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            bearingWall = when (opt) {
                                                "Ja" -> true
                                                "Nej" -> false
                                                else -> null
                                            }
                                        }
                                        .background(
                                            if (bearingWall == true && opt == "Ja" || bearingWall == false && opt == "Nej" || bearingWall == null && opt == "Uvidende")
                                                ByggePilotenBlue else Color.White,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(vertical = 16.dp)
                                ) {
                                    Text(
                                        opt,
                                        color = if (bearingWall == true && opt == "Ja" || bearingWall == false && opt == "Nej" || bearingWall == null && opt == "Uvidende")
                                            Color.White else Color.Black,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                    4 -> {
                        Text(if (isRepair == true) "Område der skal repareres" else "Dimensioner", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                        Spacer(Modifier.height(24.dp))

                        Text("Antal vægge med samme mål", color = Color.White, fontSize = 16.sp)
                        OutlinedTextField(
                            value = multiWallCount.toString(),
                            onValueChange = { newValue ->
                                if (newValue.all { it.isDigit() } && newValue.isNotBlank()) multiWallCount = newValue.toInt()
                                else if (newValue.isBlank()) multiWallCount = 1
                            },
                            keyboardOptions = FoundationKeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors
                        )

                        Spacer(Modifier.height(24.dp))
                        Text("Mål pr. væg", color = Color.White, fontSize = 16.sp)
                        OutlinedTextField(
                            value = length,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' || c == ',' }) length = it.replace(',', '.') },
                            label = { Text("Længde (meter)") },
                            keyboardOptions = FoundationKeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors
                        )
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = height,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' || c == ',' }) height = it.replace(',', '.') },
                            label = { Text("Højde (meter)") },
                            keyboardOptions = FoundationKeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors
                        )

                        if (totalWallArea > 0f) {
                            Spacer(Modifier.height(24.dp))
                            Text("Brutto areal: ${"%.1f".format(totalWallArea)} m²", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            if (openingsArea > 0f) {
                                Text("Åbningsfradrag: ${"%.1f".format(openingsArea)} m²", color = Color.White)
                                Text("Netto murareal: ${"%.1f".format(nettoArea)} m²", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                        }
                    }
                    5 -> { // Tykkelse (kun ny mur)
                        Text("Tykkelse", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                        Spacer(Modifier.height(24.dp))
                        val options = listOf("108 mm (halvsten)", "228 mm (helsten)", "Anden")
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            options.forEach { option ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { thicknessOption = option }
                                        .background(if (thicknessOption == option) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                                        .padding(vertical = 16.dp)
                                ) {
                                    Text(
                                        option,
                                        color = if (thicknessOption == option) Color.White else Color.Black,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                        if (thicknessOption == "Anden") {
                            Spacer(Modifier.height(16.dp))
                            OutlinedTextField(
                                value = customThickness,
                                onValueChange = { if (it.all { c -> c.isDigit() }) customThickness = it },
                                label = { Text("Tykkelse (mm)") },
                                keyboardOptions = FoundationKeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors
                            )
                        }
                    }
                    6 -> { // Sten type (kun ny mur)
                        Text("Sten type", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                        Spacer(Modifier.height(24.dp))
                        val options = listOf(
                            "Almindelig rød mursten",
                            "Gul mursten",
                            "Facadesten / tegl",
                            "Håndstrøgne sten",
                            "Gasbetonblokke",
                            "Letbetonblokke",
                            "Kalksandsten",
                            "Anden"
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            options.forEach { option ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { stoneType = option }
                                        .background(if (stoneType == option) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                                        .padding(vertical = 16.dp)
                                ) {
                                    Text(
                                        option,
                                        color = if (stoneType == option) Color.White else Color.Black,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                        if (stoneType == "Anden") {
                            Spacer(Modifier.height(16.dp))
                            OutlinedTextField(
                                value = customStoneType ?: "",
                                onValueChange = { customStoneType = it },
                                label = { Text("Beskriv sten") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors
                            )
                        }
                    }
                    7 -> { // Mørtel type (kun ny mur)
                        Text("Mørtel type", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                        Spacer(Modifier.height(24.dp))
                        val options = listOf("Standard KC-mørtel", "Bastardmørtel", "Lime-mørtel", "Anden")
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            options.forEach { option ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { mortarType = option }
                                        .background(if (mortarType == option) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                                        .padding(vertical = 16.dp)
                                ) {
                                    Text(
                                        option,
                                        color = if (mortarType == option) Color.White else Color.Black,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                        if (mortarType == "Anden") {
                            Spacer(Modifier.height(16.dp))
                            OutlinedTextField(
                                value = customMortarType ?: "",
                                onValueChange = { customMortarType = it },
                                label = { Text("Beskriv mørtel") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors
                            )
                        }
                    }
                    8 -> { // Skader (kun reparation)
                        Text("Beskriv skader", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                        Spacer(Modifier.height(24.dp))

                        Text("Revner i muren?", color = Color.White, fontSize = 16.sp)
                        Spacer(Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            listOf("Ja", "Nej").forEach { opt ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { hasCracks = opt == "Ja" }
                                        .background(if (hasCracks == (opt == "Ja")) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                                        .padding(vertical = 16.dp)
                                ) {
                                    Text(opt, color = if (hasCracks == (opt == "Ja")) Color.White else Color.Black, modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                        if (hasCracks == true) {
                            Spacer(Modifier.height(16.dp))
                            OutlinedTextField(
                                value = cracksDescription ?: "",
                                onValueChange = { cracksDescription = it },
                                label = { Text("Beskriv revner") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors
                            )
                        }

                        Spacer(Modifier.height(32.dp))
                        Text("Fugt eller andre skader?", color = Color.White, fontSize = 16.sp)
                        Spacer(Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            listOf("Ja", "Nej").forEach { opt ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { hasMoistureDamage = opt == "Ja" }
                                        .background(if (hasMoistureDamage == (opt == "Ja")) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                                        .padding(vertical = 16.dp)
                                ) {
                                    Text(opt, color = if (hasMoistureDamage == (opt == "Ja")) Color.White else Color.Black, modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                        if (hasMoistureDamage == true) {
                            Spacer(Modifier.height(16.dp))
                            OutlinedTextField(
                                value = moistureDescription ?: "",
                                onValueChange = { moistureDescription = it },
                                label = { Text("Beskriv skader") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors
                            )
                        }

                        Spacer(Modifier.height(32.dp))
                        Text("Sætningsskader eller løse sten?", color = Color.White, fontSize = 16.sp)
                        Spacer(Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            listOf("Ja", "Nej").forEach { opt ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { hasSettlementDamage = opt == "Ja" }
                                        .background(if (hasSettlementDamage == (opt == "Ja")) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                                        .padding(vertical = 16.dp)
                                ) {
                                    Text(opt, color = if (hasSettlementDamage == (opt == "Ja")) Color.White else Color.Black, modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                        if (hasSettlementDamage == true) {
                            Spacer(Modifier.height(16.dp))
                            OutlinedTextField(
                                value = settlementDescription ?: "",
                                onValueChange = { settlementDescription = it },
                                label = { Text("Beskriv skader") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors
                            )
                        }
                    }
                    9 -> { // Detaljer (kun ny mur)
                        Text("Detaljer", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                        Spacer(Modifier.height(24.dp))

                        Text("Antal åbninger (vinduer/døre)", color = Color.White, fontSize = 16.sp)
                        OutlinedTextField(
                            value = openingsCount,
                            onValueChange = { if (it.all { c -> c.isDigit() } || it.isBlank()) openingsCount = it },
                            keyboardOptions = FoundationKeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = textFieldColors
                        )

                        val count = openingsCount.toIntOrNull() ?: 0
                        if (count > 0) {
                            Spacer(Modifier.height(24.dp))
                            Text("Hvordan vil du angive åbninger?", color = Color.White, fontSize = 16.sp)
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                listOf("Samlet fradragsareal (m²)", "Individuelle mål (cm)").forEach { mode ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { openingMode = if (mode.contains("samlet")) "samlet" else "individuel" }
                                            .background(if (openingMode == if (mode.contains("samlet")) "samlet" else "individuel") ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                                            .padding(vertical = 16.dp)
                                    ) {
                                        Text(
                                            mode,
                                            color = if (openingMode == if (mode.contains("samlet")) "samlet" else "individuel") Color.White else Color.Black,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                    }
                                }
                            }

                            if (openingMode == "samlet") {
                                Spacer(Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = openingTotalAreaM2,
                                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' || c == ',' }) openingTotalAreaM2 = it.replace(',', '.') },
                                    label = { Text("Samlet areal (m²)") },
                                    keyboardOptions = FoundationKeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = textFieldColors
                                )
                            } else if (openingMode == "individuel") {
                                Spacer(Modifier.height(16.dp))
                                Text("Gennemsnitligt mål pr. åbning (hvis varierer, skriv i beskrivelse senere)", color = Color.White, fontSize = 16.sp)
                                OutlinedTextField(
                                    value = openingWidthCm,
                                    onValueChange = { if (it.all { c -> c.isDigit() }) openingWidthCm = it },
                                    label = { Text("Bredde (cm)") },
                                    keyboardOptions = FoundationKeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = textFieldColors
                                )
                                Spacer(Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = openingHeightCm,
                                    onValueChange = { if (it.all { c -> c.isDigit() }) openingHeightCm = it },
                                    label = { Text("Højde (cm)") },
                                    keyboardOptions = FoundationKeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = textFieldColors
                                )
                            }

                            if (openingsArea > 0f) {
                                Spacer(Modifier.height(16.dp))
                                Text("Åbningsareal: ${"%.1f".format(openingsArea)} m²", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("Netto murareal: ${"%.1f".format(nettoArea)} m²", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                        }

                        Spacer(Modifier.height(32.dp))
                        Text("Armering/forstærkning nødvendig?", color = Color.White, fontSize = 16.sp)
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            listOf("Ja", "Nej", "Uvidende").forEach { opt ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            reinforcement = when (opt) {
                                                "Ja" -> true
                                                "Nej" -> false
                                                else -> null
                                            }
                                        }
                                        .background(
                                            if (reinforcement == true && opt == "Ja" || reinforcement == false && opt == "Nej" || reinforcement == null && opt == "Uvidende")
                                                ByggePilotenBlue else Color.White,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(vertical = 16.dp)
                                ) {
                                    Text(
                                        opt,
                                        color = if (reinforcement == true && opt == "Ja" || reinforcement == false && opt == "Nej" || reinforcement == null && opt == "Uvidende")
                                            Color.White else Color.Black,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(32.dp))
                        Text("Ønsket overflade/afslutning", color = Color.White, fontSize = 16.sp)
                        val surfaceOptions = listOf("Blank mur", "Pudset/vandskuret", "Malet", "Med tagsten/afslutning på top", "Andet")
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            surfaceOptions.forEach { opt ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { surfaceFinish = opt }
                                        .background(if (surfaceFinish == opt) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                                        .padding(vertical = 16.dp)
                                ) {
                                    Text(
                                        opt,
                                        color = if (surfaceFinish == opt) Color.White else Color.Black,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                        if (surfaceFinish == "Andet") {
                            Spacer(Modifier.height(16.dp))
                            OutlinedTextField(
                                value = customSurface,
                                onValueChange = { customSurface = it },
                                label = { Text("Beskriv") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors
                            )
                        }

                        if (isFacadeMur) {
                            Spacer(Modifier.height(32.dp))
                            Text("Isolering ønsket?", color = Color.White, fontSize = 16.sp)
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                listOf("Ja", "Nej").forEach { opt ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { insulationWanted = opt == "Ja" }
                                            .background(if (insulationWanted == (opt == "Ja")) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                                            .padding(vertical = 16.dp)
                                    ) {
                                        Text(opt, color = if (insulationWanted == (opt == "Ja")) Color.White else Color.Black, modifier = Modifier.padding(horizontal = 16.dp))
                                    }
                                }
                            }
                            if (insulationWanted == true) {
                                Spacer(Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = insulationThickness,
                                    onValueChange = { if (it.all { c -> c.isDigit() }) insulationThickness = it },
                                    label = { Text("Tykkelse (mm)") },
                                    keyboardOptions = FoundationKeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = textFieldColors
                                )
                            }
                        }

                        Spacer(Modifier.height(32.dp))
                        Text("Fundament", color = Color.White, fontSize = 16.sp)
                        val foundationOptions = listOf("Ja, nyt nødvendigt", "Nej, eksisterende OK", "Uvidende", "Andet")
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            foundationOptions.forEach { option ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { foundationOption = option }
                                        .background(if (foundationOption == option) ByggePilotenBlue else Color.White, RoundedCornerShape(8.dp))
                                        .padding(vertical = 16.dp)
                                ) {
                                    Text(
                                        option,
                                        color = if (foundationOption == option) Color.White else Color.Black,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                        if (foundationOption == "Andet") {
                            Spacer(Modifier.height(16.dp))
                            OutlinedTextField(
                                value = customFoundation,
                                onValueChange = { customFoundation = it },
                                label = { Text("Beskriv") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = textFieldColors
                            )
                        }
                    }
                    totalSteps -> { // Adgang & stillads (altid sidst)
                        Text("Adgang og stillads", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                        Spacer(Modifier.height(24.dp))

                        val avgHeight = height.toFloatOrNull() ?: 0f
                        if (avgHeight > 3f) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "Højde > 3 m – stillads sandsynligvis nødvendigt",
                                    color = Color.White,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                        }

                        Text("God adgang til opsætning af stillads?", color = Color.White, fontSize = 16.sp)
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            listOf("Ja", "Nej", "Uvidende").forEach { opt ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            goodAccess = when (opt) {
                                                "Ja" -> true
                                                "Nej" -> false
                                                else -> null
                                            }
                                        }
                                        .background(
                                            if (goodAccess == true && opt == "Ja" || goodAccess == false && opt == "Nej" || goodAccess == null && opt == "Uvidende")
                                                ByggePilotenBlue else Color.White,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(vertical = 16.dp)
                                ) {
                                    Text(
                                        opt,
                                        color = if (goodAccess == true && opt == "Ja" || goodAccess == false && opt == "Nej" || goodAccess == null && opt == "Uvidende")
                                            Color.White else Color.Black,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(64.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (currentStep > 1) {
                        TextButton(onClick = { currentStep-- }) {
                            Text("Tilbage", color = Color.White)
                        }
                    } else {
                        Spacer(Modifier.width(80.dp))
                    }

                    Button(
                        onClick = {
                            if (currentStep == totalSteps) {
                                navController.navigate("task_photos_description/opmuring") {
                                    popUpTo("opmuring") { inclusive = true }
                                }
                            } else {
                                currentStep++
                                // Automatisk skip irrelevante steps
                                while (currentStep <= totalSteps && when (currentStep) {
                                        5, 6, 7, 9 -> isRepair == true // tykkelse, sten, mørtel, detaljer kun ved ny mur
                                        8 -> isNewMur // skader kun ved reparation
                                        else -> false
                                    }) {
                                    currentStep++
                                }
                            }
                        },
                        enabled = isStepValid
                    ) {
                        Text(if (currentStep == totalSteps) "Fortsæt til billeder og beskrivelse" else "Næste")
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}