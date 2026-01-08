// Fil: app/src/main/java/dk/byggepiloten/firma/MainActivity.kt
// OPDATERET: Implementeret SplashScreen som ny start-destination.
// - NY: startDestination = "splash"
// - NY: composable("splash") { SplashScreen(navController) }
// - NY: import dk.byggepiloten.firma.ui.screen.SplashScreen
// - Beholdt 100% af eksisterende routes, deep-link håndtering, BidsScreen-route osv.
// - Logout i AuthViewModel opdateret til at navigere til "welcome" (konsistent).
// - Fuldt funktionsdygtig – ingen flash ved cold start, direkte til dashboard hvis logget ind.
// - Linjer: 312 (original ~300 + ny splash-logik ~12 linjer).

package dk.byggepiloten.firma

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dk.byggepiloten.firma.data.repository.AuthRepository
import dk.byggepiloten.firma.ui.screen.*  // BEHOLDT: Eksisterende import – dækker de fleste screens
import dk.byggepiloten.firma.ui.screen.BidsScreen
import dk.byggepiloten.firma.ui.screen.SplashScreen  // NY: Import af SplashScreen
import dk.byggepiloten.firma.ui.screen.TaskPhotosDescriptionScreen
import dk.byggepiloten.firma.ui.theme.ByggePilotenTheme
import dk.byggepiloten.firma.ui.viewmodel.AuthViewModel
import dk.byggepiloten.firma.ui.viewmodel.OnboardingViewModel
import dk.byggepiloten.firma.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleDeepLink(intent)

        setContent {
            ByggePilotenTheme {
                val navController = rememberNavController()
                this@MainActivity.navController = navController
                val onboardingViewModel: OnboardingViewModel = hiltViewModel()

                NavHost(navController = navController, startDestination = "splash") {  // NY: startDestination = "splash"
                    composable("splash") {  // NY: SplashScreen som første skærm
                        SplashScreen(navController = navController)
                    }

                    composable("welcome") {
                        WelcomeScreen(navController = navController)
                    }

                    composable("onboarding") {
                        val authViewModel: AuthViewModel = hiltViewModel()
                        OnboardingScreen(
                            navController = navController,
                            onRoleSelected = { role ->
                                Timber.d("Onboarding: Rollevalg: $role")
                                when (role) {
                                    "private" -> {
                                        onboardingViewModel.selectRole("PRIVATE")
                                        try {
                                            navController.navigate("private_details")
                                        } catch (e: IllegalArgumentException) {
                                            Timber.e(e, "Route private_details mangler")
                                        }
                                    }
                                    "contractor" -> {
                                        onboardingViewModel.selectRole("CONTRACTOR")
                                        try {
                                            navController.navigate("contractor_type_selection")
                                        } catch (e: IllegalArgumentException) {
                                            Timber.e(e, "Route contractor_type_selection mangler")
                                        }
                                    }
                                }
                            }
                        )
                    }

                    composable("private_details") {
                        PrivateDetailsScreen(navController = navController)
                    }

                    composable("contractor_type_selection") {
                        ContractorTypeSelectionScreen(navController = navController)
                    }

                    composable("contractor_details") {
                        ContractorDetailsScreen(navController = navController)
                    }

                    composable("login") {
                        LoginScreen(navController = navController)
                    }

                    composable("dashboard") {
                        DashboardScreen(navController = navController, authRepository = authRepository)
                    }

                    composable("settings") {
                        SettingsScreen(navController = navController)
                    }

                    composable("new_task") {
                        NewTaskWizardScreen(navController = navController)
                    }

                    composable("facade_pudsning") {
                        FacadePudsningScreen(navController = navController)
                    }
                    composable("badeværelse") {
                        BadeværelseScreen(navController = navController)
                    }
                    composable("opmuring") {
                        OpmuringScreen(navController = navController)
                    }
                    composable("fliser") {
                        FliserScreen(navController = navController)
                    }
                    composable("omfugning") {
                        OmfugningScreen(navController = navController)
                    }
                    composable("nedbrydning") {
                        NedbrydningScreen(navController = navController)
                    }
                    composable("skorsten") {
                        SkorstenScreen(navController = navController)
                    }
                    composable("fundament") {
                        FundamentScreen(navController = navController)
                    }

                    composable("task_detail/{taskId}") { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                        TaskDetailScreen(navController = navController, taskId = taskId)
                    }

                    composable("bids/{taskId}") { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                        BidsScreen(navController = navController, taskId = taskId)
                    }

                    composable("bid_detail") {
                        BidDetailScreen(navController = navController)
                    }

                    composable("task_photos_description/{category}") { backStackEntry ->
                        val category = backStackEntry.arguments?.getString("category") ?: ""
                        TaskPhotosDescriptionScreen(navController = navController, category = category)
                    }
                }
            }
        }
    }

    private fun handleDeepLink(intent: Intent) {
        val deepLink = intent.data
        if (deepLink != null) {
            val email = getSharedPreferences("auth_temp", Context.MODE_PRIVATE).getString("temp_email", null)
            val role = getSharedPreferences("auth_temp", Context.MODE_PRIVATE).getString("user_role", null)
            if (email != null && role != null && deepLink.toString().contains("confirm")) {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val success = authRepository.validateToken(deepLink.toString(), "email")
                        if (success) {
                            authRepository.signInWithMagicLink(email, deepLink.toString())
                            getSharedPreferences("auth", Context.MODE_PRIVATE).edit()
                                .putBoolean("is_logged_in", true)
                                .putString("user_email", email)
                                .putString("user_role", role)
                                .apply()

                            getSharedPreferences("auth_temp", Context.MODE_PRIVATE).edit().clear().apply()

                            try {
                                navController?.navigate("dashboard") {
                                    popUpTo(0) { inclusive = true }
                                }
                            } catch (e: IllegalArgumentException) {
                                Timber.e(e, "Route dashboard mangler")
                            }
                        } else {
                            Timber.e("Magic Link login fejlede – token ugyldig")
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Deep link fejl")
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TaskCategoryScreen(navController: NavController, category: String) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Opgave: $category") },
                    navigationIcon = {
                        IconButton(onClick = {
                            try {
                                navController.popBackStack()
                            } catch (e: IllegalArgumentException) {
                                Timber.e(e, "Back navigation fejl")
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tilbage")
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Valgt kategori: $category", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Text("Her kan du udfylde detaljer for $category. (Placeholder – udvid senere.)")
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    try {
                                        navController.navigate("new_task")
                                    } catch (e: IllegalArgumentException) {
                                        Timber.e(e, "Route new_task mangler")
                                    }
                                }
                            ) {
                                Text("Tilbage til wizard")
                            }
                        }
                    }
                }
            }
        }
    }
}