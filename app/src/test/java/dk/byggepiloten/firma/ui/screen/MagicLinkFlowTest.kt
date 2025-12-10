// app/src/test/java/dk/byggepiloten/firma/ui/screen/MagicLinkFlowTest.kt
// OPDATERET: Rettet unresolved references – tilføjet imports for HiltAndroidTest, HiltAndroidRule, MainActivity.
// Beholdt alt andet 100% uændret.

package dk.byggepiloten.firma.ui.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.testing.HiltAndroidRule  // NYT: Import for HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest  // NYT: Import for HiltAndroidTest
import dk.byggepiloten.firma.MainActivity  // NYT: Import for MainActivity
import io.mockk.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = dagger.hilt.android.testing.HiltTestApplication::class)
class MagicLinkFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockUser: FirebaseUser

    @Before
    fun setUp() {
        hiltRule.inject()
        ShadowLog.stream = System.out

        // MOCK FirebaseApp.getInstance() – RETTER IllegalStateException
        mockkStatic(FirebaseApp::class)
        val mockFirebaseApp = mockk<FirebaseApp>(relaxed = true)
        every { FirebaseApp.getInstance() } returns mockFirebaseApp

        mockAuth = mockk(relaxed = true)
        mockUser = mockk(relaxed = true)

        mockkStatic(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns mockAuth

        val mockTask = mockk<com.google.android.gms.tasks.Task<com.google.firebase.auth.AuthResult>>(relaxed = true)
        every { mockTask.isSuccessful } returns true
        every { mockTask.result?.user } returns mockUser
        every { mockUser.uid } returns "test-uid-123"

        every { mockAuth.isSignInWithEmailLink(any()) } returns true
        every { mockAuth.signInWithEmailLink(any(), any()) } returns mockTask

        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("auth_temp", 0)
        prefs.edit().putString("email_for_signin", "test@byggepiloten.dk").apply()
    }

    @Test
    fun `magic link deep link logs in and navigates to dashboard`() = runBlocking {
        composeTestRule.apply {
            onNodeWithText("ByggePiloten").assertIsDisplayed()

            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://byggepiloten.firebaseapp.com/?email=test@byggepiloten.dk&role=private")
            }

            composeTestRule.activity.onNewIntent(intent)

            delay(2000)

            // TILPAS TIL DIN DASHBOARD (brug en tekst fra DashboardScreen.kt)
            onNodeWithText("Velkommen", ignoreCase = true).assertExists()
        }
    }
}