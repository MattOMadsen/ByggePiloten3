package dk.byggepiloten.firma

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dk.byggepiloten.firma.data.repository.AuthRepository
import dk.byggepiloten.firma.ui.screen.*
import dk.byggepiloten.firma.ui.theme.ByggePilotenTheme
import dk.byggepiloten.firma.ui.viewmodel.AuthViewModel
import dk.byggepiloten.firma.ui.viewmodel.OnboardingViewModel
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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    this@MainActivity.navController = navController
                    val onboardingViewModel: OnboardingViewModel = hiltViewModel()

                    NavHost(navController = navController, startDestination = "onboarding") {
                        composable("onboarding") {
                            val authViewModel: AuthViewModel = hiltViewModel()
                            OnboardingScreen(
                                navController = navController,
                                onRoleSelected = { role -> onboardingViewModel.selectRole(role) }
                            )
                        }

                        composable("private_details") {
                            PrivateDetailsScreen(
                                navController = navController,
                                onboardingViewModel = onboardingViewModel,
                                onComplete = {
                                    navController.navigate("dashboard") { popUpTo("onboarding") { inclusive = true } }
                                }
                            )
                        }
                        composable("contractor_details") {
                            ContractorDetailsScreen(
                                navController = navController,
                                onboardingViewModel = onboardingViewModel,
                                onComplete = {
                                    navController.navigate("firma_price_setup") { popUpTo("contractor_details") { inclusive = true } }
                                }
                            )
                        }
                        composable("firma_price_setup") {
                            FirmaPriceSetupScreen(
                                navController = navController,
                                onComplete = {
                                    navController.navigate("dashboard") { popUpTo("onboarding") { inclusive = true } }
                                }
                            )
                        }
                        composable("new_task") { NewTaskWizardScreen(navController = navController) }
                        composable("facade_pudsning") { FacadePudsningScreen(navController) }
                        composable("opmuring") { OpmuringScreen(navController) }
                        composable("fliser") { FliserScreen(navController) }
                        composable("omfugning") { OmfugningScreen(navController) }
                        composable("nedbrydning") { NedbrydningScreen(navController) }
                        composable("skorsten") { SkorstenScreen(navController) }
                        composable("fundament") { FundamentScreen(navController) }
                        composable("task_photos_description") { TaskPhotosDescriptionScreen(navController = navController) }
                        composable("contractor_bids") { ContractorBidsScreen(navController = navController) }
                        composable("dashboard") { DashboardScreen(navController = navController, authRepository = authRepository) }
                        composable("login") { LoginScreen(navController = navController) }
                        composable("magic_link_sent") { MagicLinkSentScreen(navController = navController) }
                        composable("confirm") { ConfirmScreen(navController = navController, token = "") }
                        composable("check_email") { CheckEmailScreen(navController = navController) }
                        composable("settings") { SettingsScreen(navController = navController) }  // BEHOLDT: Matcher din uploadede SettingsScreen.kt.
                        composable("bid_detail") { BidDetailScreen(navController = navController) }  // BEHOLDT: Matcher ny BidDetailScreen.kt.
                        composable("price_preview") { PricePreviewScreen(onSendToBid = { navController.navigate("bid_detail") }) }  // RETTET: Matcher din uploadede PricePreviewScreen.kt (onSendToBid lambda – nav til bid_detail).
                        composable("bid_dialog") { BidDialogScreen(requestId = "placeholder_id", onDismiss = { navController.popBackStack() }) }  // RETTET: Matcher din uploadede BidDialogScreen.kt (requestId placeholder, onDismiss pop).
                        composable("bid_sent") { BidSentScreen(onBack = { navController.popBackStack() }) }  // RETTET: Matcher din uploadede BidSentScreen.kt (onBack pop).
                        composable("contractor_dashboard") { ContractorDashboardScreen(navController = navController) }  // RETTET: Matcher din uploadede ContractorDashboardScreen.kt (tilføjet navController).
                        composable("tasks") { TasksScreen(navController = navController) }  // TILFØJET: Matcher din uploadede TasksScreen.kt (tilføjet rute fra DashboardScreen bottomBar).
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent) {
        if (intent.action != Intent.ACTION_VIEW) return
        val uri: Uri? = intent.data ?: return
        val emailLink = uri.toString()

        Timber.d("Magic Link modtaget: $emailLink")

        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        if (!auth.isSignInWithEmailLink(emailLink)) return

        val email = getSharedPreferences("auth_temp", Context.MODE_PRIVATE)
            .getString("email_for_signin", null) ?: return

        val role = uri?.getQueryParameter("role") ?: "private"

        auth.signInWithEmailLink(email, emailLink)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user ?: return@addOnCompleteListener
                    Timber.d("Magic Link login succes: ${user.uid}")

                    getSharedPreferences("auth_persistent", Context.MODE_PRIVATE).edit()
                        .putString("user_uid", user.uid)
                        .putString("user_role", role)
                        .apply()

                    getSharedPreferences("auth_temp", Context.MODE_PRIVATE).edit().clear().apply()

                    navController?.navigate("dashboard") {
                        popUpTo(0) { inclusive = true }
                    }
                } else {
                    Timber.e(task.exception, "Magic Link login fejlede")
                }
            }
    }
}