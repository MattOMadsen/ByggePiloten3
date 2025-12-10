// EmailService.kt: Retrofit interface for WP e-mail-sending (send-email route).
// FIXED: Skiftet Response<Unit> til Response<Map<String, Any>> – parser 'url' fra WP JSON-body (full deep-link).
//     - Trin 1: @POST("send-email") matcher rest-api.php route (hyphen, ingen .php).
//     - Trin 2: Response<Map> tillader body-parsing i AuthRepositoryImpl_WP (val fullUrl = body["url"]).
//     - Trin 3: Body: EmailRequest (JSON: email, role, body, confirmation_url) – WP håndterer multipart HTML.
//     - Brug: Kall i sendWelcomeEmail → log "Link: byggepiloten://confirm?token=..." (til test i Timber).
//     - MVVM: Injektér via Hilt (AppProvidesModule); asynk suspend for non-blokerende UI.
//     - Sikkerhed: HTTPS baseUrl i Retrofit; args sanitized i WP (sanitize_email etc.).
//     - Offline-first: Kun net-kald (fallback false i impl ved UnknownHost).
//     - Test: Kall sendEmail → 200 OK + body["url"] i Logcat; mail med clickable button i inbox.
//     - Performance: Retrofit + Gson (gratis); <1s kald (GsonConverterFactory).

package dk.byggepiloten.firma.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface EmailService {
    @POST("send-email")  // FIXED: Hyphen, ingen .php – matcher WP-route.
    suspend fun sendEmail(@Body request: EmailRequest): Response<Map<String, Any>>  // FIXED: Map for body-parsing ('url', 'token')
}