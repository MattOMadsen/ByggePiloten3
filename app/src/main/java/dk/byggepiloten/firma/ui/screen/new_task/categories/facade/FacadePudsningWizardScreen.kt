package dk.byggepiloten.firma.ui.screen.new_task.categories.facade

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dk.byggepiloten.firma.ui.viewmodel.task.FacadeTaskViewModel

@Composable
fun FacadePudsningWizardScreen(
    navController: NavHostController // Changed to NavHostController to match standard
) {
    val childNavController = rememberNavController()
    val viewModel: FacadeTaskViewModel = hiltViewModel()

    NavHost(navController = childNavController, startDestination = "facade_area") {
        composable("facade_area") { FacadeAreaStep(navController = childNavController, viewModel = viewModel) }
        composable("facade_vaegtype") { FacadeVaegtypeStep(navController = childNavController, viewModel = viewModel) }
        composable("facade_hoejde") { FacadeHoejdeStep(navController = childNavController, viewModel = viewModel) }
        composable("facade_stillads") { FacadeStilladsStep(navController = childNavController, viewModel = viewModel) }
        composable("facade_underlag") { FacadeUnderlagStep(navController = childNavController, viewModel = viewModel) }
        composable("facade_vejret") { FacadeVejrStep(navController = childNavController, viewModel = viewModel) }
        composable("facade_armering_isolering") { FacadeArmeringIsoleringStep(navController = childNavController, viewModel = viewModel) }
        composable("facade_haeftemoertel") { FacadeHaeftemoertelStep(navController = childNavController, viewModel = viewModel) }
        composable("facade_opsummering") { FacadeOpsummeringScreen(navController = navController, viewModel = viewModel) }
    }
}
