// app/src/test/java/dk/byggepiloten/firma/FirmaPriceTest.kt
// OPDATERET: Rettet unresolved references – tilføjet imports for HiltAndroidTest, HiltAndroidRule, ComponentActivity.
// Beholdt alt andet 100% uændret.

package dk.byggepiloten.firma

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavHostController
import dagger.hilt.android.testing.HiltAndroidRule  // NYT: Import for HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest  // NYT: Import for HiltAndroidTest
import dk.byggepiloten.firma.ui.screen.FirmaPriceSetupScreen
import dk.byggepiloten.firma.ui.theme.ByggePilotenTheme
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import androidx.activity.ComponentActivity  // NYT: Import for ComponentActivity

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = dagger.hilt.android.testing.HiltTestApplication::class)
class FirmaPriceTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val mockNavController: NavHostController = mockk(relaxed = true)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun `test save prices button is displayed and clickable`() {
        composeTestRule.setContent {
            ByggePilotenTheme {
                FirmaPriceSetupScreen(
                    navController = mockNavController,
                    onComplete = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Send og fortsæt").assertIsDisplayed()

        composeTestRule.onNodeWithText("Send og fortsæt").performClick()
    }
}