package dk.byggepiloten.firma.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,  // UUID fra generateUserId()
    val name: String? = null,  // Privat: navn; Firma: companyName
    val companyName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val cvr: String? = null,  // Kun firmaer
    val bankAccount: String? = null,  // Kun tilbyder
    val materialProfitPctGlobal: Float? = null,  // Kun tilbyder/søger hvis nødvendigt
    val role: String,  // "PRIVATE", "COMPANY_SEEKER", "COMPANY_PROVIDER"
    val gdprAccepted: Boolean,
    val createdAt: Long = Date().time  // For 24h-sletning
)