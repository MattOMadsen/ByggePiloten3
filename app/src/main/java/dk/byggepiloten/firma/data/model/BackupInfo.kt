package dk.byggepiloten.firma.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dk.byggepiloten.firma.data.database.Converters  // RETTET: Import for type converters (matcher din upload)

@Entity(tableName = "backups")
@TypeConverters(Converters::class)  // RETTET: Tilføjet for List<String> eller non-primitive felter (løser "cannot figure out how to save/read this field")
data class BackupInfo(
    @PrimaryKey val id: Int,  // Auto-genereret eller timestamp
    val firmaId: Int,
    val version: String,
    val backupTimestamp: String,
    val retentionDays: Int,
    val prices: List<String> = emptyList()  // BEHOLDT: List<String> med converter (JSON-stringe af prices – løser type converter-fejl)
)