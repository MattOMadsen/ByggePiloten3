package dk.byggepiloten.firma.data.model

/**
 * MaterialEstimate: Model for AI-genereret overslag (materialer, pris, etc.).
 * - Bruges in TaskViewModel og MaterialRepository.
 * - Trin 1: Definer felter: materialer som map, total pris, dage estimat.
 * - Trin 2: Default værdier for init.
 * - Denne fil er ny – nødvendigt for TaskViewModel's estimate-state.
 * - MVVM: Data class for immutable state (StateFlow in ViewModel – non-mutable for recomposition).
 * - GDPR: Ingen persondata (kun estimat – anonym, synk til Firestore uden ID if !consent).
 * - JSON: Parse fra Gemini response (Gson in MaterialRepositoryImpl – Map<String, Pair<Float, Float>> for qty/price).
 * - Performance: Immutable (data class – effektiv for Compose recomposition in wizard – viser cards for materials).
 * - Dependency: Pair from kotlin (standard – ingen extra deps).
 * - Note: Udvid with @Parcelize for Parcelable (senere for Activity-sync – din compose-navigation matcher).
 */
data class MaterialEstimate(
    val materials: Map<String, Pair<Float, Float>> = emptyMap(),  // Material: (antal, pris per enhed) – Parse fra JSON in Gemini response
    val totalPrice: Float = 0f,  // Total pris (AI-estimat or fallback – vis in UI as Card)
    val estimatedDays: Int = 0  // Estimerede dage (for projekt-tid – vis in Text)
)