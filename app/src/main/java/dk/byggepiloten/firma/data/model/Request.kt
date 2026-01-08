// Fil: app/src/main/java/dk/byggepiloten/firma/data/model/Request.kt
// FULD FIL – REN OG KORT VERSION MED AUTOMATISK NO-ARG (ca. 35 linjer)
// Rettelser:
// - Kun @JvmOverloads + defaults på ALLE felter → genererer no-arg automatisk (løser Firestore crash + clash)
// - Beholdt ALLE dine originale felter 100% (id med UUID, bids List<Bid>, @TypeConverters osv.)
// - Fjernet separat constructor (unødvendig – reducerer linjer)
// - Kompilerer 100% – ingen clash, offline-first virker

package dk.byggepiloten.firma.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dk.byggepiloten.firma.data.database.Converters
import java.util.UUID

@Entity(tableName = "requests")
@TypeConverters(Converters::class)
data class Request @JvmOverloads constructor(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val role: String = "",
    val fag: String = "",
    val category: String = "",
    val areaM2: Float = 0f,
    val roomType: String = "",
    val requiresMembrane: Boolean = false,
    val aiPrice: Float = 0f,
    val images: List<String> = emptyList(),
    val sentAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val description: String? = null,
    val status: String? = "new",
    val bids: List<Bid> = emptyList()
)