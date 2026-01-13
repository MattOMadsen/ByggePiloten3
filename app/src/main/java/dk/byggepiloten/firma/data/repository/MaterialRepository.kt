package dk.byggepiloten.firma.data.repository

import dk.byggepiloten.firma.data.model.price.MaterialEstimate  // Model for estimat (fra oversigt: materials-map, totalPrice)
import kotlinx.coroutines.flow.Flow

/**
 * MaterialRepository: Interface for AI-prisoverslag (Gemini Nano offline + fallback-priser).
 * - MVVM: Injektér i TaskViewModel for wizard-beregning (areal, rum, category → estimat).
 * - Offline-first: Gemini Nano for on-device AI (gratis via ML Kit GenAI; temperature 0.7f, maxOutputTokens 512).
 * - Fallback: Hardcoded priser (f.eks. 650 kr/m² Opmuring, 500 kr/m² Badeværelse fra NetpudsCategory).
 * - Reactive: Flow<MaterialEstimate> for loading-state i UI (Compose: collectAsState).
 * - Features: estimatePrice(area: Float, room: String, category: String) – Return estimat med materials-map.
 * Trin 1: estimatePrice() – Suspend for AI-kald (Generation.getClient().generateContent med request: TextPart(prompt)).
 * Trin 2: getFallbackPrices() – Flow for hardcoded map (brug hvis AI offline/fejl).
 * Fix: Ny interface – Løser dependency for TaskViewModel (injektér via Hilt @Binds).
 * Brug: I TaskViewModel: repo.estimatePrice(areaM2, roomType, category) – StateFlow<MaterialEstimate?> for UI.
 * Dependency: ML Kit GenAI Prompt 1.0.0-alpha1 (implementation("com.google.mlkit:genai-prompt:1.0.0-alpha1")) – Offline via AICore.
 * Note: Prompt-template: "Estimat pris for [category] i [room], areal [area] m², inkl. materialer og fortjeneste – JSON-output".
 */
interface MaterialRepository {

    /**
     * Beregn pris-estimat med AI (Gemini Nano offline).
     * - Prompt: Baseret på areal, rum, category (f.eks. "Opmuring i Køkken, 20 m²").
     * - Return MaterialEstimate (totalPrice, materials: Map<String, Pair<Float, Float>>).
     * - Kaster Exception ved fejl (f.eks. AI-init – fallback i ViewModel).
     */
    suspend fun estimatePrice(areaM2: Float, roomType: String, category: String): MaterialEstimate

    /**
     * Hent fallback-priser som Flow (hardcoded for offline-sikkerhed).
     * - Map: Category → basePrice (f.eks. "Opmuring" → 650f kr/m²).
     * - Brug hvis AI fejler (total = basePrice * areaM2).
     */
    fun getFallbackPrices(): Flow<Map<String, Float>>
}