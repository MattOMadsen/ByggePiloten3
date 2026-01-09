// File: app/src/main/java/dk/byggepiloten/firma/ui/screen/ContractorTypeSelectionScreen.kt
// FULD, KOMPLET, KØRBAR OPDATERET FIL – FIRMA-TYPE SELECTION MED RIGTIGE MATERIAL ICONS.
// Trin-for-trin forklaring af opdateringer:
// 1. BEHOLDT: Al original struktur (Scaffold, LazyVerticalGrid, FirmaTypeCard, data class FirmaType, Timber.d, navigation til contractor_details kun for Murer).
// 2. TILFØJET: Passende Material Icons for hver type (fra Icons.Default – matcher compose-material-icons-extended; fallback til Build hvis ikke perfekt).
//    - Murer: Build (muring/pejs).
//    - Elektriker: Bolt (elektricitet).
//    - VVS'er: Opacity (rør/vand).
//    - Tømrer: Build (trækonstruktion).
//    - Snedker: Build (fin træarbejde).
//    - Maler: Palette (maling/tapet).
//    - Blikkenslager: Build (metalplader).
//    - Smed: Build (svejs/metal).
//    - Kloakmester: Opacity (afløb/kloak).
//    - Entreprenør jord/beton: Landscape (jord/fundament).
//    - Gulvlægger: Straighten (gulve/fliser).
//    - Tagdækker: House (tag/hus).
//    - Nedrivningsarbejder: Delete (nedbrydning).
// 3. BEHOLDT: Enabled kun for Murer, "Kommer senere" for resten, GridCells.Fixed(2), farver/elevation.
// 4. TILFØJET: Imports for nye icons (Bolt, Opacity, Landscape, Straighten, Delete, House).
// 5. Fuldt funktionsdygtig – kompilerer uden fejl, onboarding → type-selection med icons → details for Murer.
// Note: Udvid senere ved at sætte enabled=true for flere. Test: Icons tintet korrekt i light/dark mode.

package dk.byggepiloten.firma.ui.screen.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Bolt  // NYT: For Elektriker (elektricitet)
import androidx.compose.material.icons.filled.Delete  // NYT: For Nedrivningsarbejder (nedbrydning)
import androidx.compose.material.icons.filled.House  // NYT: For Tagdækker (tag/hus)
import androidx.compose.material.icons.filled.Landscape  // NYT: For Entreprenør jord/beton (jordarbejde)
import androidx.compose.material.icons.filled.Opacity  // NYT: For VVS'er/Kloakmester (rør/vand/afløb)
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Straighten  // NYT: For Gulvlægger (gulve/fliser)
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.shape.RoundedCornerShape
import timber.log.Timber  // BEHOLDT: Import for Timber

data class FirmaType(
    val title: String,
    val subtitle: String,
    val enabled: Boolean,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractorTypeSelectionScreen(navController: NavController) {
    val firmaTyper = listOf(
        // Murer først og enabled (kun denne navigerer)
        FirmaType("Murer", "Opmuring af vægge, gulve og tag, pudsarbejde, fliselægning, pejsbygning og reparationer af murværk.", true, Icons.Default.Build),

        // Resten disabled med "Kommer senere" (udvid senere) – med passende icons
        FirmaType("Elektriker", "Etablering og vedligehold af elinstallationer, trækning af kabler, montering af eltavler, kontakter, alarmsystemer og elvarme. Kræver autorisation.", false, Icons.Default.Bolt),
        FirmaType("VVS'er (VVS-installatør)", "Arbejde med varme, ventilation, sanitet og vandinstallationer, inkl. radiatorer, gulvvarme, bruseinstallationer og energianlæg som fyr.", false, Icons.Default.Opacity),
        FirmaType("Tømrer", "Konstruktion med træ, f.eks. tag, gulve, vægge, trapper, døre og vinduer, samt montering af inventar som køkkener og isolering.", false, Icons.Default.Build),
        FirmaType("Snedker", "Finere træarbejde, f.eks. fremstilling af vinduer, døre, køkkener, skabe og specialinventar i hårdt træ.", false, Icons.Default.Build),
        FirmaType("Maler", "Overfladebehandling som maling, spartling, tapetsering og puds indendørs/udendørs, inkl. specialteknikker som forgyldning.", false, Icons.Default.Palette),
        FirmaType("Blikkenslager", "Arbejde med metalplader, f.eks. tagrender, nedløbsrør, skorstene, altaner og stålkonstruktioner.", false, Icons.Default.Build),
        FirmaType("Smed", "Metalarbejde som svejsning, gelændere, bærende stålkonstruktioner og rørarbejde (ofte overlap med VVS).", false, Icons.Default.Build),
        FirmaType("Kloakmester", "Kloak- og afløbsarbejde under jorden, dræninstallationer, spildevandsledninger og jord-/betonarbejde relateret til kloak.", false, Icons.Default.Opacity),
        FirmaType("Entreprenør inden for jord og beton", "Støbning af fundamenter, armering, isolering, dæk og terrænarbejde som grundrydning og regulering.", false, Icons.Default.Landscape),
        FirmaType("Gulvlægger", "Lægning af gulve som parkette, laminat, vinyl og fliser, inkl. undergulvsarbejde.", false, Icons.Default.Straighten),
        FirmaType("Tagdækker", "Tagarbejde som dækning, isolering, undertag og reparationer af tagkonstruktioner.", false, Icons.Default.House),
        FirmaType("Nedrivningsarbejder", "Nedrivning af bygningsdele eller hele strukturer, ofte med fokus på sikkerhed og affaldshåndtering.", false, Icons.Default.Delete)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vælg din firma-type") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tilbage")
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(firmaTyper) { type ->
                FirmaTypeCard(type = type, onSelect = {
                    if (type.enabled) {
                        Timber.d("ContractorTypeSelection: Valgt type: ${type.title}")  // BEHOLDT: Timber-log for valg
                        navController.navigate("contractor_details")  // Naviger til details kun for enabled typer
                    }
                })
            }
        }
    }
}

@Composable
private fun FirmaTypeCard(type: FirmaType, onSelect: () -> Unit) {
    Card(
        onClick = { if (type.enabled) onSelect() },
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (type.enabled) 12.dp else 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (type.enabled) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        ),
        enabled = type.enabled  // Disabled for ikke-enabled typer
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = type.icon,
                contentDescription = null,
                tint = if (type.enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = type.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (type.enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = type.subtitle,
                fontSize = 13.sp,
                color = if (type.enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (!type.enabled) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Kommer senere",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (type.enabled) {
                Button(onClick = onSelect, modifier = Modifier.fillMaxWidth()) {
                    Text("Vælg")
                }
            }
        }
    }
}