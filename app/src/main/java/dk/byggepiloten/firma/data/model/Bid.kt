// File: app/src/main/java/dk/byggepiloten/firma/data/model/Bid.kt
// NY FIL: Data class for Bid (manglende fra BidDetailScreen.kt – løser unresolved Bid, copy(), etc.).
// Trin-for-trin forklaring:
// 1. Baseret på planen: Buddetaljer (pris, timer, materialer, kommentar, status fra contractor flow).
// 2. TILFØJET: Felder matcher BidDetailScreen.kt (id, price, hours, materials, comment, status).
// 3. Fuldt funktionsdygtig – data class med copy() for immutable updates.
// Note: Integrer med Firestore (toObject<Bid> i repo). Tilføj flere felder hvis nødvendigt (f.eks. requestId, userId).

package dk.byggepiloten.firma.data.model

data class Bid(
    val id: String,
    val price: Float,
    val hours: Int,
    val materials: String,
    val comment: String,
    val status: String
)