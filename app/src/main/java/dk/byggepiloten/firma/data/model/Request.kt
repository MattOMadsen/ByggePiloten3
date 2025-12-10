package dk.byggepiloten.firma.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dk.byggepiloten.firma.data.database.Converters  // BEHOLDT: Import for type converters (matcher din upload)
import java.util.UUID  // BEHOLDT: Tilføjet import for resolved

@Entity(tableName = "requests")
@TypeConverters(Converters::class)  // BEHOLDT: Tilføjet for List<String> images (løser "cannot figure out how to save/read this field")
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
    val status: String? = "new"  // e.g., "new", "pending", "completed" (brug i filter-logik)
)