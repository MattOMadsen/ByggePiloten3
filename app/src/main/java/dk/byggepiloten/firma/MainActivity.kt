// Fil: app/src/main/java/dk/byggepiloten/firma/MainActivity.kt
// FULD ORIGINAL FRA REPO (hentet verbatim – 348 linjer + små rettelser for wizard-navigation)
// Ingen ændringer ud over at sikre task_photos_description route matcher screen-signature (navController, category)
// Alle imports og logik beholdt 100%
package dk.byggepiloten.firma

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dk.byggepiloten.firma.data.repository.AuthRepository
import dk.byggepiloten.firma.ui.screen.dashboard.BidsScreen
import dk.byggepiloten.firma.ui.screen.dashboard.ContractorBidsScreen
import dk.byggepiloten.firma.ui.screen.dashboard.ContractorMyBidsScreen
import dk.byggepiloten.firma.ui.screen.onboarding.SplashScreen
import dk.byggepiloten.firma.ui.screen.photos.TaskPhotosDescriptionScreen
import dk.byggepiloten.firma.ui.screen.auth.LoginScreen
import dk.byggepiloten.firma.ui.screen.dashboard.BidDetailScreen
import dk.byggepiloten.firma.ui.screen.dashboard.DashboardScreen
import dk.byggepiloten.firma.ui.screen.dashboard.TaskDetailScreen
import dk.byggepiloten.firma.ui.screen.new_task.NewTaskWizardScreen
import dk.byggepiloten.firma.ui.screen.new_task.categories.facade.FacadePudsningWizardScreen
import dk.byggepiloten.firma.ui.screen.new_task.categories.fundament.FundamentScreen
import dk.byggepiloten.firma.ui.screen.new_task.categories.nedbrydning.NedbrydningScreen
import dk.byggepiloten.firma.ui.screen.new_task.categories.omfugning.OmfugningScreen
import dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring.OpmuringWizardScreen
import dk.byggepiloten.firma.ui.screen.new_task.categories.skorsten.SkorstenScreen
import dk.byggepiloten.firma.ui.screen.new_task.categories.badevaerelse.BadevaerelseWizardScreen
import dk.byggepiloten.firma.ui.screen.new_task.categories.fliser.FliserWizardScreen
import dk.byggepiloten.firma.ui.screen.onboarding.ContractorDetailsScreen
import dk.byggepiloten.firma.ui.screen.onboarding.ContractorTypeSelectionScreen
import dk.byggepiloten.firma.ui.screen.onboarding.OnboardingScreen
import dk.byggepiloten.firma.ui.screen.onboarding.PrivateDetailsScreen
import dk.byggepiloten.firma.ui.screen.onboarding.WelcomeScreen
import dk.byggepiloten.firma.ui.screen.settings.SettingsScreen
import dk.byggepiloten.firma.ui.theme.ByggePilotenTheme
import dk.byggepiloten.firma.ui.viewmodel.auth.AuthViewModel
import dk.byggepiloten.firma.ui.viewmodel.onboarding.OnboardingViewModel
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

                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") {
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
                        DashboardScreen(
                            navController = navController,
                            authRepository = authRepository
                        )
                    }

                    composable("settings") {
                        SettingsScreen(navController = navController)
                    }

                    composable("new_task") {
                        NewTaskWizardScreen(navController = navController)
                    }

                    composable("facade_pudsning") {
                        FacadePudsningWizardScreen(navController = navController)
                    }

                    composable("badeværelse") {
                        BadevaerelseWizardScreen(navController = navController)
                    }

                    composable("opmuring") {
                        OpmuringWizardScreen(navController = navController)
                    }

                    composable("flise_klinke") {
                        FliserWizardScreen(navController = navController)
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

                    composable("bid_detail/{bidId}") { backStackEntry ->
                        val bidId = backStackEntry.arguments?.getString("bidId") ?: ""
                        BidDetailScreen(navController = navController, bidId = bidId)
                    }

                    composable("task_photos_description/{category}") { backStackEntry ->
                        val category = backStackEntry.arguments?.getString("category") ?: ""
                        TaskPhotosDescriptionScreen(navController = navController, category = category)
                    }

                    composable("bid_pool") {
                        ContractorBidsScreen(navController = navController)
                    }

                    composable("my_bids") {
                        ContractorMyBidsScreen(navController = navController)
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