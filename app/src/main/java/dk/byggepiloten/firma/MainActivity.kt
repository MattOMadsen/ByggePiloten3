// Fil: app/src/main/java/dk/byggepiloten/firma/MainActivity.kt

package dk.byggepiloten.firma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import dk.byggepiloten.firma.ui.screen.dashboard.ImagesScreen
import dk.byggepiloten.firma.ui.screen.dashboard.FullDetailsScreen
import dk.byggepiloten.firma.ui.screen.onboarding.SplashScreen
import dk.byggepiloten.firma.ui.screen.photos.TaskPhotosDescriptionScreen
import dk.byggepiloten.firma.ui.screen.auth.LoginScreen
import dk.byggepiloten.firma.ui.screen.dashboard.BidDetailScreen
import dk.byggepiloten.firma.ui.screen.dashboard.DashboardScreen
import dk.byggepiloten.firma.ui.screen.dashboard.TaskDetailScreen
import dk.byggepiloten.firma.ui.screen.new_task.NewTaskWizardScreen
import dk.byggepiloten.firma.ui.screen.new_task.categories.fundament.FundamentScreen
import dk.byggepiloten.firma.ui.screen.new_task.categories.nedbrydning.NedbrydningScreen
import dk.byggepiloten.firma.ui.screen.new_task.categories.omfugning.OmfugningScreen
import dk.byggepiloten.firma.ui.screen.new_task.categories.opmuring.OpmuringWizardScreen
import dk.byggepiloten.firma.ui.screen.new_task.categories.skorsten.SkorstenScreen
import dk.byggepiloten.firma.ui.screen.new_task.categories.badevaerelse.BadevaerelseWizardScreen
import dk.byggepiloten.firma.ui.screen.new_task.categories.fliser.FliserWizardScreen
import dk.byggepiloten.firma.ui.screen.new_task.categories.puds.PudsWizardScreen
import dk.byggepiloten.firma.ui.screen.onboarding.ContractorDetailsScreen
import dk.byggepiloten.firma.ui.screen.onboarding.ContractorTypeSelectionScreen
import dk.byggepiloten.firma.ui.screen.onboarding.OnboardingScreen
import dk.byggepiloten.firma.ui.screen.onboarding.PrivateDetailsScreen
import dk.byggepiloten.firma.ui.screen.onboarding.WelcomeScreen
import dk.byggepiloten.firma.ui.screen.settings.SettingsScreen
import dk.byggepiloten.firma.ui.theme.ByggePilotenTheme
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("MainActivity.onCreate() kaldt – start")

        setContent {
            ByggePilotenTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen(navController = navController)
                    }

                    composable("welcome") {
                        WelcomeScreen(navController = navController)
                    }

                    composable("onboarding") {
                        OnboardingScreen(
                            navController = navController,
                            onRoleSelected = { role ->
                                if (role == "private") {
                                    navController.navigate("private_details")
                                } else {
                                    navController.navigate("contractor_type")
                                }
                            }
                        )
                    }

                    composable("contractor_type") {
                        ContractorTypeSelectionScreen(
                            navController = navController
                        )
                    }

                    composable("private_details") {
                        PrivateDetailsScreen(
                            navController = navController
                        )
                    }

                    composable("contractor_details") {
                        ContractorDetailsScreen(
                            navController = navController
                        )
                    }

                    composable("login") {
                        LoginScreen(
                            navController = navController
                        )
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

                    composable("pudsning") {
                        PudsWizardScreen(navController = navController)
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

                    composable("task_images/{taskId}") { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                        ImagesScreen(navController = navController, taskId = taskId)
                    }

                    composable("task_full_details/{taskId}") { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                        FullDetailsScreen(navController = navController, taskId = taskId)
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

        Timber.d("MainActivity.onCreate() færdig")
    }

    override fun onResume() {
        super.onResume()
        Timber.d("MainActivity.onResume() kaldt")
    }
}
