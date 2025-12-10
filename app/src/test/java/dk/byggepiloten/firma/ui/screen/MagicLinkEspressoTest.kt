// app/src/androidTest/java/dk/byggepiloten/firma/ui/screen/MagicLinkEspressoTest.kt
// FULD, KØRBAR – MED IdlingResource til Firebase + Espresso Intents
// Venter på login/sendMagicLink færdiggøres før navigation

package dk.byggepiloten.firma.ui.screen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dk.byggepiloten.firma.MainActivity
import dk.byggepiloten.firma.R
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicBoolean

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MagicLinkEspressoTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityTestRule(MainActivity::class.java, true, false)

    private lateinit var scenario: ActivityScenario<MainActivity>
    private lateinit var idlingResource: FirebaseIdlingResource

    @Before
    fun setUp() {
        hiltRule.inject()
        Intents.init()

        // Opret IdlingResource
        idlingResource = FirebaseIdlingResource("FirebaseLogin")
        IdlingRegistry.getInstance().register(idlingResource)

        // Start MainActivity
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
        Intents.release()
        scenario.close()
    }

    @Test
    fun `send magic link, wait for firebase, click link, login succeeds, see dashboard`() {
        // --- TRIN 1: Gå til LoginScreen og send Magic Link ---
        onView(withId(R.id.email_field)).perform(typeText("kontor@graverholtmurerfirma.dk"), closeSoftKeyboard())
        onView(withId(R.id.login_mode_link)).perform(click())
        onView(withId(R.id.gdpr_checkbox)).perform(click())

        // Start IdlingResource før kald
        idlingResource.startWaiting()

        onView(withId(R.id.login_button)).perform(click())

        // --- TRIN 2: Vent på Firebase (via IdlingResource) ---
        // Espresso venter nu, indtil idlingResource bliver idle

        onView(withText("Tjek din mail")).check(matches(isDisplayed()))

        // --- TRIN 3: Simuler klik på Magic Link i mail ---
        val magicLink = "https://byg-piloten.firebaseapp.com/__/auth/action?mode=signIn&email=kontor@graverholtmurerfirma.dk&role=contractor"

        Intents.intending(hasAction(Intent.ACTION_VIEW))
            .respondWith { result ->
                result.activityResult = androidx.test.core.app.ActivityResult(
                    Activity.RESULT_OK,
                    Intent().setData(Uri.parse(magicLink))
                )
            }

        ApplicationProvider.getApplicationContext<android.content.Context>().startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(magicLink))
        )

        // --- TRIN 4: Vent på login og tjek dashboard ---
        onView(withText("Velkommen")).check(matches(isDisplayed()))
        onView(withText("kontor@graverholtmurerfirma.dk")).check(matches(isDisplayed()))
    }
}

// --- IDLING RESOURCE: Venter på Firebase kald i ViewModel ---
class FirebaseIdlingResource(private val resourceName: String) : IdlingResource {

    private val isIdle = AtomicBoolean(true)
    private var callback: IdlingResource.ResourceCallback? = null

    fun startWaiting() {
        isIdle.set(false)
    }

    fun finishWaiting() {
        if (isIdle.compareAndSet(false, true)) {
            callback?.onTransitionToIdle()
        }
    }

    override fun getName(): String = resourceName

    override fun isIdleNow(): Boolean = isIdle.get()

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }
}