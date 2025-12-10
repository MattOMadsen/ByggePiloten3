// File: app/src/androidTest/java/dk/byggepiloten/firma/ui/screen/MagicLinkAndPasswordEspressoTest.kt
// UPDATED: Full onboarding + login flow for privat kunde (per plan: Onboarding → PrivateDetails → Login → Dashboard)
// Mocks: saveRole(role: String), sendMagicLink(email, role), login(email, password)
// Tags: Assume from screens (tilføj testTag if needed: e.g. Modifier.testTag("role_card_private"))

package dk.byggepiloten.firma.ui.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dk.byggepiloten.firma.MainActivity
import dk.byggepiloten.firma.data.repository.AuthRepository
import dk.byggepiloten.firma.di.AppBindsModule
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.rules.RuleChain
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(AppBindsModule::class)
@RunWith(AndroidJUnit4::class)
class MagicLinkAndPasswordEspressoTest {

    private val hiltRule = HiltAndroidRule(this)
    private val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val ruleChain: RuleChain = RuleChain.outerRule(hiltRule).around(composeTestRule)

    @Inject
    lateinit var authRepository: AuthRepository

    @Before
    fun setup() = runTest {
        hiltRule.inject()

        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.getNavController()?.navigate("onboarding") {
                popUpTo(0) { inclusive = true }
            }
        }

        composeTestRule.waitForIdle()
    }

    // NY: Onboarding – vælg privat kunde → navigér til PrivateDetailsScreen
    @Test
    fun testOnboarding_SelectPrivateRole_NavigatesToPrivateDetails() = runTest {
        with(composeTestRule) {
            onNodeWithTag("onboarding_screen").assertIsDisplayed()
            onNodeWithTag("role_card_private").performClick()  // Tilpas tag for privat-kort
            onNodeWithTag("continue_button").performClick()

            waitUntil(5000) {
                onAllNodesWithTag("private_details_screen").fetchSemanticsNodes().size == 1
            }
            onNodeWithTag("private_details_screen").assertIsDisplayed()
        }
    }

    // NY: Onboarding + details – vælg rolle, gem details → navigér til LoginScreen
    @Test
    fun testOnboardingAndDetails_SavePrivateDetails_NavigatesToLogin() = runTest {
        coEvery { authRepository.saveRole(eq("private")) } returns Unit  // Mock rolle-gemning

        with(composeTestRule) {
            onNodeWithTag("onboarding_screen").assertIsDisplayed()
            onNodeWithTag("role_card_private").performClick()
            onNodeWithTag("continue_button").performClick()

            waitUntil(5000) {
                onAllNodesWithTag("private_details_screen").fetchSemanticsNodes().size == 1
            }
            onNodeWithTag("private_details_screen").assertIsDisplayed()

            // Simuler input i details (navn, adresse, email, GDPR)
            onNodeWithTag("name_field").performTextInput("Test Kunde")  // Tilpas tags
            onNodeWithTag("address_field").performTextInput("Testvej 1")
            onNodeWithTag("email_field_details").performTextInput("test@byggepiloten.dk")
            onNodeWithTag("gdpr_checkbox").performClick()
            onNodeWithTag("save_button").performClick()

            waitUntil(5000) {
                onAllNodesWithTag("login_screen").fetchSemanticsNodes().size == 1
            }
            onNodeWithTag("login_screen").assertIsDisplayed()
        }
    }

    @Test
    fun testMagicLink_WithInvalidEmail_ShowsError_PasswordFieldNotVisible() = runTest {
        coEvery { authRepository.sendMagicLink(any<String>(), any<String>()) } returns false

        with(composeTestRule) {
            onNodeWithTag("login_screen").assertIsDisplayed()
            onNodeWithTag("radio_link").performClick()
            onNodeWithTag("email_field").performTextInput("forkert@email.dk")
            onNodeWithTag("send_button").performClick()

            onNodeWithTag("error_message", useUnmergedTree = true)
                .assertIsDisplayed()
                .assertTextContains("Kunne ikke sende link")

            onAllNodesWithTag("password_field").assertCountEquals(0)
        }
    }

    @Test
    fun testMagicLink_WithValidEmail_NavigatesToMagicLinkSent() = runTest {
        coEvery { authRepository.sendMagicLink(eq("kontor@graverholtmurerfirma.dk"), eq("private")) } returns true

        with(composeTestRule) {
            onNodeWithTag("login_screen").assertIsDisplayed()
            onNodeWithTag("radio_link").performClick()
            onNodeWithTag("email_field").performTextInput("kontor@graverholtmurerfirma.dk")
            onNodeWithTag("send_button").performClick()

            onAllNodesWithTag("error_message").assertCountEquals(0)

            waitUntil(5000) {
                onAllNodesWithTag("magic_link_sent_screen").fetchSemanticsNodes().size == 1
            }
            onNodeWithTag("magic_link_sent_screen").assertIsDisplayed()
        }
    }

    @Test
    fun testPasswordLogin_WithInvalidCredentials_ShowsError() = runTest {
        coEvery { authRepository.login(any<String>(), any<String>()) } returns false

        with(composeTestRule) {
            onNodeWithTag("login_screen").assertIsDisplayed()
            onNodeWithTag("radio_password").performClick()
            onNodeWithTag("email_field").performTextInput("forkert@email.dk")
            onNodeWithTag("password_field").performTextInput("forkertpass")
            onNodeWithTag("send_button").performClick()

            onNodeWithTag("error_message", useUnmergedTree = true)
                .assertIsDisplayed()
                .assertTextContains("Forkert email eller password")

            onNodeWithTag("login_screen").assertIsDisplayed()
        }
    }

    @Test
    fun testPasswordLogin_WithValidCredentials_NavigatesToDashboard() = runTest {
        coEvery { authRepository.login(eq("kontor@graverholtmurerfirma.dk"), eq("sikkerpass123")) } returns true

        with(composeTestRule) {
            onNodeWithTag("login_screen").assertIsDisplayed()
            onNodeWithTag("radio_password").performClick()
            onNodeWithTag("email_field").performTextInput("kontor@graverholtmurerfirma.dk")
            onNodeWithTag("password_field").performTextInput("sikkerpass123")
            onNodeWithTag("send_button").performClick()

            onAllNodesWithTag("error_message").assertCountEquals(0)

            waitUntil(5000) {
                onAllNodesWithTag("dashboard_screen").fetchSemanticsNodes().size == 1
            }
            onNodeWithTag("dashboard_screen").assertIsDisplayed()
        }
    }
}