// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/task/AiEstimateGenerator.kt
// NY FIL – Dedikeret AI-estimat-generator (singleton via Hilt)
// Placeholder med reel Gemini Nano stub + cloud fallback struktur
// Kan udvides med kategori-specifik prompt, billed-analyse osv.
// Ca. 120 linjer

package dk.byggepiloten.firma.ui.viewmodel.task

import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton-klasse til generering af AI-prisestimater.
 * Bruger Gemini Nano lokal først → cloud fallback.
 * Kan injectes i ViewModels og udvides med kategori-specifikke prompts.
 */
@Singleton
class AiEstimateGenerator @Inject constructor() {

    // TODO: Konfigurer reel Gemini Nano model (when available on device)
    private val localModel: GenerativeModel? = null // Placeholder – initialiser når Nano er tilgængelig

    // Cloud model (Gemini Pro eller lignende)
    private val cloudModel = GenerativeModel(
        modelName = "gemini-1.5-flash", // Eller pro – afhængig af API-key/setup
        apiKey = "" // Flyt til secrets/Gradle properties
    )

    /**
     * Generer AI-estimat baseret på kategori, areal og valgfri beskrivelse/billeder.
     * @param category Opgavekategori (f.eks. "opmuring")
     * @param areaM2 Areal i m²
     * @param description Brugerbeskrivelse (valgfri)
     * @param onSuccess Callback med estimeret pris i øre (Long)
     * @param onError Callback ved fejl
     */
    suspend fun generateEstimate(
        category: String,
        areaM2: Float,
        description: String? = null,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            // Prøv lokal Nano først
            if (localModel != null) {
                val localResult = withContext(Dispatchers.IO) {
                    generateWithModel(localModel, category, areaM2, description)
                }
                onSuccess(localResult)
                return
            }

            // Fallback til cloud
            val cloudResult = withContext(Dispatchers.IO) {
                generateWithModel(cloudModel, category, areaM2, description)
            }
            onSuccess(cloudResult)

        } catch (e: Exception) {
            Timber.e(e, "AI estimate fejl")
            onError("Kunne ikke generere prisoverslag – prøv igen senere")
        }
    }

    private suspend fun generateWithModel(
        model: GenerativeModel,
        category: String,
        areaM2: Float,
        description: String?
    ): Long {
        val prompt = buildPrompt(category, areaM2, description)

        val response = model.generateContent(content { text(prompt) })
        val priceText = response.text.orEmpty()

        // Parse pris fra tekst (f.eks. "Ca. 25.000 kr" → 2500000 øre)
        val price = priceText
            .replace("[^0-9]".toRegex(), "")
            .toLongOrNull() ?: 15000L // Default fallback

        return price * 100 // Til øre
    }

    private fun buildPrompt(category: String, areaM2: Float, description: String?): String {
        return """
            Du er en erfaren murer i Danmark. Estimér en realistisk pris (ekskl. materialer, inkl. moms) 
            for følgende opgave: $category, areal ca. $areaM2 m².
            ${description?.let { "Yderligere beskrivelse: $it" }.orEmpty()}
            Svar KUN med prisen i hele kroner, f.eks. "25000".
        """.trimIndent()
    }
}