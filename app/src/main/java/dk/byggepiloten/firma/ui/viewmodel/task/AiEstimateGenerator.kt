// Fil: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/task/AiEstimateGenerator.kt
// FIX: Fjernet *100 (AI svarer i hele kroner)
// - Fallback direkte i kroner (35.000 kr for 25 m² standard)

package dk.byggepiloten.firma.ui.viewmodel.task

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import dk.byggepiloten.firma.BuildConfig

@Singleton
class AiEstimateGenerator @Inject constructor() {

    private val localModel: GenerativeModel? = null

    private val cloudModel by lazy {
        if (BuildConfig.GEMINI_API_KEY.isBlank()) {
            Timber.w("GEMINI_API_KEY mangler – cloud deaktiveret, bruger fallback")
            null
        } else {
            Timber.d("Initialiserer Gemini cloud model med key (længde: ${BuildConfig.GEMINI_API_KEY.length})")
            GenerativeModel(
                modelName = "gemini-2.5-flash",
                apiKey = BuildConfig.GEMINI_API_KEY
            )
        }
    }

    suspend fun generateEstimate(
        category: String,
        areaM2: Float,
        description: String? = null,
        extraDetails: String? = null,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        val effectiveArea = areaM2.coerceAtLeast(1f)

        try {
            localModel?.let { model ->
                val price = withContext(Dispatchers.IO) {
                    generateWithModel(model, category, effectiveArea, description, extraDetails)
                }
                onSuccess(price)
                return
            }

            cloudModel?.let { model ->
                val price = withContext(Dispatchers.IO) {
                    generateWithModel(model, category, effectiveArea, description, extraDetails)
                }
                onSuccess(price)
                return
            }

            val fallbackPrice = calculateSimpleEstimate(category, effectiveArea)
            onSuccess(fallbackPrice)
            onError("AI ikke tilgængelig – viser groft estimat baseret på areal")

        } catch (e: Exception) {
            Timber.e(e, "Reel fejl i AI-generation")
            val fallbackPrice = calculateSimpleEstimate(category, effectiveArea)
            onSuccess(fallbackPrice)
            onError("Kunne ikke kontakte AI – viser groft estimat")
        }
    }

    private suspend fun generateWithModel(
        model: GenerativeModel,
        category: String,
        areaM2: Float,
        description: String?,
        extraDetails: String?
    ): Long {
        val prompt = buildPrompt(category, areaM2, description, extraDetails)
        Timber.d("Prompt sendt til AI:\n$prompt")

        val response = model.generateContent(content { text(prompt) })
        val priceText = response.text.orEmpty()
        Timber.d("AI svar: $priceText")

        val cleaned = priceText.replace(Regex("[^0-9]"), "")
        if (cleaned.isEmpty()) throw IllegalArgumentException("AI returnerede ingen pris")

        return cleaned.toLong() // Ingen *100 – AI svarer i hele kroner
    }

    private fun buildPrompt(
        category: String,
        areaM2: Float,
        description: String?,
        extraDetails: String?
    ): String {
        return """
            Du er en erfaren murer i Danmark i 2026.
            Estimér realistisk totalpris for arbejdsløn (ekskl. materialer, inkl. moms) for $category på ${"%.2f".format(areaM2)} m².
            
            Typiske danske priser 2026:
            - Standard ny opmuring: 800–1500 kr/m²
            - Reparation eller bærende væg: 1200–2000 kr/m²
            - Med puds, armering eller specialsten: +15–30 %
            
            ${description?.let { "Kundens beskrivelse: $it" }.orEmpty()}
            ${extraDetails?.let { "Yderligere detaljer: $it" }.orEmpty()}
            
            Giv KUN prisen i hele kroner som rent tal (f.eks. 45000). Ingen tekst, ingen interval.
        """.trimIndent()
    }

    private fun calculateSimpleEstimate(category: String, areaM2: Float): Long {
        val pricePerM2 = when (category.lowercase()) {
            "opmuring" -> 1400L // Ca. 35.000 kr for 25 m²
            "facade" -> 1600L
            "fliser" -> 1200L
            "badeværelse" -> 2000L
            else -> 1400L
        }
        return pricePerM2 * areaM2.toLong()
    }
}