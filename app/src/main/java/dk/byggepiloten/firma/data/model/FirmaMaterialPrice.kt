package dk.byggepiloten.firma.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * FirmaMaterialPrice: Model for firma-priser (fra oversigt).
 * - Pris + % fortjeneste.
 * - Bruges i Room og CSV-import/export.
 * - GDPR: Anonymiseret i eksport.
 */
@Entity(tableName = "firma_materials")
data class FirmaMaterialPrice(
    @PrimaryKey val material: String,
    val customPrice: Float,
    val unit: String,  // RETTET: Required parameter
    val profitPct: Float
)