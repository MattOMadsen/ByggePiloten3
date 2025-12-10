// File: app/src/main/java/dk/byggepiloten/firma/ui/SignInLinkHandlerActivity.kt
// NY FIL: Oprettet for at håndtere deep link fra Magic Link (nødvendigt – kaldes fra Manifest intent-filter).
// Forklaring trin-for-trin: Hent email fra SharedPrefs, kald authRepository.signInWithMagicLink, naviger til Dashboard hvis succes.
// NYT TILFØJET: Brug Hilt for injection, lifecycleScope for coroutines.
// NYT TILFØJET: Håndter fejl med Toast og finish().
// NYT FIX: Ændret til dk.byggepiloten.firma.MainActivity::class.java – løser unresolved 'MainActivity'.
// Fuldt funktionsdygtig – matcher opdaterede docs (nov 2025), sikrer sign-in på samme device.

package dk.byggepiloten.firma.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dk.byggepiloten.firma.MainActivity  // NYT FIX: Import for MainActivity
import dk.byggepiloten.firma.data.repository.AuthRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SignInLinkHandlerActivity : ComponentActivity() {

    @Inject lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val emailLink = intent.data?.toString()
        if (emailLink.isNullOrEmpty()) {
            Timber.w("No email link in intent")
            Toast.makeText(this, "Ugyldigt link", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val prefs = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val email = prefs.getString("pending_email", null)
        if (email.isNullOrEmpty()) {
            Timber.e("No pending email stored")
            Toast.makeText(this, "Ingen e-mail gemt – prøv igen", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            val success = authRepository.signInWithMagicLink(email, emailLink)
            if (success) {
                Timber.d("Magic link verification succes – navigér til dashboard")
                startActivity(Intent(this@SignInLinkHandlerActivity, MainActivity::class.java))
            } else {
                Toast.makeText(this@SignInLinkHandlerActivity, "Link verification mislykkedes", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
}