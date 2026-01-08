// Fil: app/src/main/java/dk/byggepiloten/firma/data/model/Bid.kt
// OPDATERET: Tilføjet contractorId, contractorName, timestamp og status med defaults.
// Trin-for-trin kommentarer:
// 1. Originale felter beholdt 100%.
// 2. Nye felter gør det muligt at vise firma-navn, tidspunkt og status i BidsScreen.
// 3. Defaults sikrer ingen null-fejl ved Firestore/Room deserialization.
// 4. Senere: Når bid sendes (i BidViewModel.sendBid), sæt contractorName fra current user (f.eks. via Auth).

package dk.byggepiloten.firma.data.model

data class Bid(
    val id: String = "",
    val contractorId: String = "",
    val contractorName: String = "",
    val price: Float = 0f,
    val hours: Int = 0,
    val materials: String = "",
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "pending"
)