// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/task/AiEstimateGenerator.kt
// FIX: Reel cloud API-key håndtering + sikker fallback til simpel beregning
// - API-key loades nu fra BuildConfig.GEMINI_API_KEY (skal sættes i gradle.properties + build.gradle)
// - Bedre error-handling med specifikke beskeder
// - Fallback: Simpel areal-baseret estimat (3000-5000 kr/m² afhængig kategori) hvis både local + cloud fejler
// - Local Nano stadig placeholder (kan aktiveres når tilgængelig)
// - Parse forbedret med bedre regex + validation
// - Mere logging for debugging
// Total lines: 168

package dk.byggepiloten.firma.ui.viewmodel.task

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import dk.byggepiloten.firma.BuildConfig

/**
 * Singleton-klasse til generering af AI-prisestimater.
 * Prioritet: Gemini Nano (lokal) → Gemini Cloud → Simpel fallback-beregning.
 * API-key håndteres sikkert via BuildConfig (sæt i gradle.properties: GEMINI_API_KEY="din-key").
 */
@Singleton
class AiEstimateGenerator @Inject constructor() {

    // Placeholder for Gemini Nano – aktiver når model er tilgængelig på device
    private val localModel: GenerativeModel? = null

    // Cloud model – key fra BuildConfig (aldrig hardcode!)
    private val cloudModel by lazy {
        if (BuildConfig.GEMINI_API_KEY.isBlank()) {
            Timber.w("Gemini API-key mangler i BuildConfig – cloud deaktiveret")
            null
        } else {
            GenerativeModel(
                modelName = "gemini-1.5-flash", // Hurtig + billig – skift til pro ved behov
                apiKey = BuildConfig.GEMINI_API_KEY
            )
        }
    }

    /**
     * Generer estimat.
     * @param category Opgavekategori (f.eks. "opmuring")
     * @param areaM2 Netto areal i m²
     * @param description Valgfri brugerbeskrivelse
     * @param onSuccess Callback med pris i øre (Long)
     * @param onError Callback med fejlbesked
     */
    suspend fun generateEstimate(
        category: String,
        areaM2: Float,
        description: String? = null,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            // 1. Prøv lokal Nano
            if (localModel != null) {
                Timber.d("Prøver Gemini Nano lokal")
                val localResult = withContext(Dispatchers.IO) {
                    generateWithModel(localModel, category, areaM2, description)
                }
                onSuccess(localResult)
                return
            }

            // 2. Prøv cloud hvis key findes
            cloudModel?.let { model ->
                Timber.d("Prøver Gemini Cloud")
                val cloudResult = withContext(Dispatchers.IO) {
                    generateWithModel(model, category, areaM2, description)
                }
                onSuccess(cloudResult)
                return
            }

            // 3. Fallback: Simpel beregning hvis ingen AI tilgængelig
            Timber.w("Ingen AI tilgængelig – bruger simpel fallback")
            val fallbackPriceKr = calculateSimpleEstimate(category, areaM2)
            onSuccess(fallbackPriceKr * 100) // Til øre
            onError("AI ikke tilgængelig – viser groft estimat baseret på areal")

        } catch (e: Exception) {
            Timber.e(e, "Kritisk fejl i AI-estimat generation")
            // Sidste fallback
            val fallbackPriceKr = calculateSimpleEstimate(category, areaM2)
            onSuccess(fallbackPriceKr * 100)
            onError("Kunne ikke kontakte AI – viser groft estimat")
        }
    }

    private suspend fun generateWithModel(
        model: GenerativeModel,
        category: String,
        areaM2: Float,
        description: String?
    ): Long {
        val prompt = buildPrompt(category, areaM2, description)
        Timber.d("AI prompt: $prompt")

        val response = model.generateContent(content { text(prompt) })
        val priceText = response.text.orEmpty()
        Timber.d("AI response: $priceText")

        // Parse kun tal (f.eks. "Ca. 25.000 kr" → 25000)
        val cleaned = priceText.replace(Regex("[^0-9]"), "")
        val priceKr = cleaned.toLongOrNull() ?: throw IllegalArgumentException("Kunne ikke parse pris fra AI-response")

        return priceKr * 100 // Til øre
    }

    private fun buildPrompt(category: String, areaM2: Float, description: String?): String {
        return """
            Du er en erfaren håndværker i Danmark specialiseret i $category.
            Estimér en realistisk pris EKSKL. materialer, INKL. moms for en opgave på ca. ${"%.2f".format(areaM2)} m².
            ${description?.let { "Kunde beskrivelse: $it" }.orEmpty()}
            
            Svar KUN med prisen i hele kroner som et rent tal, f.eks. 25000.
            Ingen forklaring, ingen tekst omkring.
        """.trimIndent()
    }

    /**
     * Simpel fallback-beregning baseret på gennemsnitlige priser pr. m² (2026-niveau).
     * Kan udvides med kategori-specifikke intervaller.
     */
    private fun calculateSimpleEstimate(category: String, areaM2: Float): Long {
        val pricePerM2 = when (category.lowercase()) {
            "opmuring" -> 3500L // Ca. gennemsnit for murerarbejde
            "facade" -> 4000L
            "fliser" -> 3000L
            "badeværelse" -> 5000L
            else -> 3500L // Default
        }
        return (pricePerM2 * areaM2.coerceAtLeast(1f)).toLong()
    }
}