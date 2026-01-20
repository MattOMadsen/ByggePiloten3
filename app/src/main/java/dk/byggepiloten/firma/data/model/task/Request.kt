// Fil: app/src/main/java/dk/byggepiloten/firma/data/model/task/Request.kt
// OPDATERET: details ændret til Map<String, Any?> (for at matche nye converter + toMap() fra data classes)
// - Ingen andre ændringer – beholdt Long for createdAt/sentAt (client-time)
// - Løser KSP/Room-fejl + type-mismatch i ViewModels
// Total lines: 92 (bekræftet)

package dk.byggepiloten.firma.data.model.task

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dk.byggepiloten.firma.data.database.Converters
import java.util.UUID

@Entity(tableName = "requests")
@TypeConverters(Converters::class)
data class Request(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    var userId: String = "",
    var role: String = "",
    var fag: String = "",
    var category: String = "",
    var areaM2: Float = 0f,
    var roomType: String = "",
    var requiresMembrane: Boolean = false,
    var aiPrice: Float = 0f,
    var images: List<String> = emptyList(),
    var sentAt: Long = System.currentTimeMillis(),
    var createdAt: Long = System.currentTimeMillis(),
    var description: String? = null,
    var status: String? = "new",
    var bids: List<Bid> = emptyList(),
    var details: Map<String, Any?> = emptyMap(), // Ændret til Any? for null-værdier fra toMap()
    var labeledPhotos: Map<String, List<String>> = emptyMap()
)