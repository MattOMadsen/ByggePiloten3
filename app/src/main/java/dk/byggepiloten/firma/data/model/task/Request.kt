// Fil: app/src/main/java/dk/byggepiloten/firma/data/model/task/Request.kt
// OPDATERET – TILFØJET labeledPhotos (Map<String, List<String>>) for at understøtte step-billeder fra wizards
// + Gjort labeledPhotos @Ignore (Room gemmer det ikke direkte – det kan parses fra details eller gemmes separat senere)
// + Beholder eksisterende felter uændret
// + ca. 45 linjer

package dk.byggepiloten.firma.data.model.task

import androidx.room.Entity
import androidx.room.Ignore
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
    var bids: List<Bid> = emptyList()
) {
    @Ignore
    var details: Map<String, Any> = emptyMap()

    // Ny: Step-billeder fra wizards (label → liste af URLs). @Ignore fordi Room ikke gemmer det direkte.
    // Kan senere gemmes i Firestore separat eller i details som JSON.
    @Ignore
    var labeledPhotos: Map<String, List<String>> = emptyMap()
}