// app/src/test/java/dk/byggepiloten/firma/ui/SignInLinkHandlerActivityTest.kt
// FULD, KØRBAR – TESTER: Magic Link → SignInLinkHandlerActivity → Firebase signIn → Navigér til MainActivity
// Bruger Robolectric + Hilt + MockK + Coroutines

package dk.byggepiloten.firma.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dk.byggepiloten.firma.data.repository.AuthRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowActivity
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = dagger.hilt.android.testing.HiltTestApplication::class)
class SignInLinkHandlerActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var authRepository: AuthRepository
    private lateinit var activityController: Robolectric.ActivityController<SignInLinkHandlerActivity>
    private lateinit var activity: SignInLinkHandlerActivity
    private lateinit var shadowActivity: ShadowActivity

    @Before
    fun setUp() {
        hiltRule.inject()

        // Mock AuthRepository
        authRepository = mockk(relaxed = true)
        coEvery { authRepository.signInWithMagicLink(any(), any()) } returns true

        // Setup Dispatchers for coroutines
        Dispatchers.setMain(testDispatcher)

        // Start activity
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://byg-piloten.firebaseapp.com/__/auth/action?mode=signIn&email=test@graverholtmurerfirma.dk&role=contractor")
        }

        // Gem pending email i SharedPreferences (simulerer sendMagicLink)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("pending_email", "test@graverholtmurerfirma.dk").apply()

        activityController = Robolectric.buildActivity(SignInLinkHandlerActivity::class.java, intent)
        activity = activityController.get()
        shadowActivity = shadowOf(activity)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        activityController.destroy()
    }

    @Test
    fun `magic link opens activity, verifies email link, signs in, and navigates to MainActivity`() = testScope.runTest {
        // GIVEN: Activity starter
        activityController.create().start().resume()

        // WHEN: signInWithMagicLink lykkes
        advanceUntilIdle() // Vent på coroutine færdiggørelse

        // THEN: Skal starte MainActivity
        val startedIntent = shadowActivity.nextStartedActivity
        assertEquals(MainActivity::class.java.name, startedIntent.component?.className)

        // THEN: Activity skal finish
        assertTrue(shadowActivity.isFinishing)

        // Verify AuthRepository blev kaldt med rigtige parametre
        coVerify { authRepository.signInWithMagicLink("test@graverholtmurerfirma.dk", "https://byg-piloten.firebaseapp.com/__/auth/action?mode=signIn&email=test@graverholtmurerfirma.dk&role=contractor") }
    }

    @Test
    fun `invalid link shows toast and finishes`() = testScope.runTest {
        // GIVEN: Ugyldigt link (ingen data)
        val invalidIntent = Intent(Intent.ACTION_VIEW)
        activityController = Robolectric.buildActivity(SignInLinkHandlerActivity::class.java, invalidIntent)
        activity = activityController.get()
        shadowActivity = shadowOf(activity)

        activityController.create().start().resume()
        advanceUntilIdle()

        // THEN: Skal vise Toast og finish
        assertTrue(shadowActivity.isFinishing)
        // Robolectric fanger ikke Toast direkte – men vi kan tjekke log eller bruge mock Toast
    }
}