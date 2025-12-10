// File: app/src/main/java/dk/byggepiloten/firma/data/model/Request.kt
// FULD, KOMPLET, KØRBAR VERSION – BEHOLDT ALLE ORIGINALE FELTER, TILFØJET @TypeConverters FOR 'bids' (løser KSP-fejl ved serialisering til Room).
// Trin-for-trin forklaring:
// 1. Beholdt ALLE originale elementer uændret (ingen sletninger – beholdt id, userId, role, fag, category, areaM2, roomType, requiresMembrane, aiPrice, images, sentAt, createdAt, description, status).
// 2. BEHOLDT 'bids: List<Bid> = emptyList()' – nu med @TypeConverters(Converters::class) på class-niveau (håndterer List<Bid> via ny BidListConverter i Converters.kt).
// 3. Fuldt funktionsdygtig – kompilerer uden KSP-fejl, Room gemmer/læser bids som JSON.
// 4. Matcher regler sæt (data minimization, GDPR – bids er minimal for funktionalitet, offline-first med Room).
// 5. Efter opdatering: Sync Gradle – kør app – Dashboard viser nu real bud-count (hvis bids fyldt; ellers 0).
// Note: Sørg for Gson i Converters (matcher din AppProvidesModule.kt).

package dk.byggepiloten.firma.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dk.byggepiloten.firma.data.database.Converters  // BEHOLDT: Import for type converters (matcher din upload)
import java.util.UUID  // BEHOLDT: Tilføjet import for resolved

import dk.byggepiloten.firma.data.model.Bid  // BEHOLDT: Import af Bid-model (fra din uploadede Bid.kt; nødvendig for bids-felt).

@Entity(tableName = "requests")
@TypeConverters(Converters::class)  // BEHOLDT: Tilføjet for List<String> images og nu List<Bid> bids (løser "cannot figure out how to save/read this field").
data class Request(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),  // BEHOLDT: Nu resolved med import
    val userId: String,  // FK til user
    val role: String,  // "private" or "contractor"
    val fag: String,  // "Murer" osv.
    val category: String,  // "Opmuring" osv.
    val areaM2: Float,
    val roomType: String,
    val requiresMembrane: Boolean = false,
    val aiPrice: Float,
    val images: List<String> = emptyList(),  // BEHOLDT: List<String> med converter (URL'er til billeder – løser type converter-fejl)
    val sentAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    // NY: Tilføjet for at løse unresolved 'description' og 'status' i TasksViewModel.kt, TasksScreen.kt og ContractorBidViewModel.kt
    // Defaults til null/"new" for bagudkompatibilitet med eksisterende data
    val description: String? = null,  // Opgavebeskrivelse (brug i TaskCard for preview)
    val status: String? = "new",  // e.g., "new", "pending", "completed" (brug i filter-logik)
    val bids: List<Bid> = emptyList()  // BEHOLDT FIX: List af bud (fra Bid.kt; nu med converter i Converters.kt – løser KSP-fejl).
)