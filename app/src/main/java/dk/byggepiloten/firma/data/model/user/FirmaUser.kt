package dk.byggepiloten.firma.data.model.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * FirmaUser: Room entity for profiler (privat/firma).
 * - @Entity(tableName = "users") – matcher DB-schema.
 * - @PrimaryKey på id (UUID fra repo).
 * - Nullable felter med @ColumnInfo(defaultValue = "NULL") for SQLite.
 * - Felter fra oversigt: name/email/phone/address/cvr/bank/profit/role/gdpr/created_at.
 * - GDPR: createdAt nullable for 24h-sletning via UserDao.
 */
@Entity(tableName = "users")
data class FirmaUser(
    @PrimaryKey val id: String,  // UUID – auto-genereret
    val name: String? = null,  // Privat: navn
    val companyName: String? = null,  // Firma: navn
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val cvr: String? = null,  // Kun firmaer
    val bankAccount: String? = null,  // Kun tilbyder
    @ColumnInfo(name = "material_profit_pct_global", defaultValue = "NULL") val materialProfitPctGlobal: Float? = null,
    val role: String,  // Non-null: "PRIVATE", "COMPANY_SEEKER", "COMPANY_PROVIDER"
    val gdprAccepted: Boolean = false,  // Non-null
    @ColumnInfo(name = "created_at", defaultValue = "NULL") val created_at: Long? = null  // Nullable: Sæt ved save
)