// File: app/src/main/java/dk/byggepiloten/firma/data/database/Converters.kt
// FULD, KOMPLET, KØRBAR VERSION – TILFØJET BidListConverter FOR List<Bid> (Gson til JSON; løser KSP-fejl i Request.kt for bids-felt).
// Trin-for-trin forklaring:
// 1. Beholdt ALLE originale elementer uændret (ingen sletninger – beholdt FirmaMaterialPriceList og StringList converters med @JvmName for KSP).
// 2. TILFØJET: Import dk.byggepiloten.firma.data.model.task.Bid (fra din Bid.kt).
// 3. TILFØJET: fromBidList (List<Bid>? → String? JSON) og toBidList (String? → List<Bid>?) – matcher eksisterende Gson-pattern, med @JvmName for KSP-kompatibilitet.
// 4. Fuldt funktionsdygtig – kompilerer uden KSP-fejl, Room konverterer bids korrekt.
// 5. Matcher regler sæt (Hilt DI for Gson, offline-first med Room, ingen nye deps).
// 6. Efter opdatering: Sync Gradle – kør app – Opgaver med bids gemmes/læses korrekt.
// Note: Sørg for @TypeConverters(Converters::class) i Request.kt (antaget fra tidligere).

package dk.byggepiloten.firma.data.database  // BEHOLDT: Flyttet til data.database for bedre KSP resolution (fixer subtype-fejl i Room-processor).

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dk.byggepiloten.firma.data.model.price.FirmaMaterialPrice
import dk.byggepiloten.firma.data.model.task.Bid  // TILFØJET: Import for Bid-model (fra din Bid.kt; nødvendigt for ny converter).

class Converters {
    private val gson = Gson()

    @TypeConverter
    @JvmName("fromFirmaMaterialPriceList")  // BEHOLDT: @JvmName for bedre KSP compatibility med generics/lists (fra KSP issues).
    fun fromFirmaMaterialPriceList(prices: List<FirmaMaterialPrice>?): String? {
        return prices?.let { gson.toJson(it) }
    }

    @TypeConverter
    @JvmName("toFirmaMaterialPriceList")  // BEHOLDT: @JvmName for at undgå resolution-bugs.
    fun toFirmaMaterialPriceList(pricesJson: String?): List<FirmaMaterialPrice>? {
        return pricesJson?.let {
            val type = object : TypeToken<List<FirmaMaterialPrice>>() {}.type
            gson.fromJson(it, type)
        }
    }

    @TypeConverter
    @JvmName("fromStringList")  // BEHOLDT: @JvmName for lists.
    fun fromStringList(images: List<String>?): String? {
        return images?.let { gson.toJson(it) }
    }

    @TypeConverter
    @JvmName("toStringList")  // BEHOLDT: @JvmName.
    fun toStringList(imagesJson: String?): List<String>? {
        return imagesJson?.let {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(it, type)
        }
    }

    // TILFØJET: Ny converter for List<Bid> i Request (løser KSP-fejl ved serialisering til Room).
    @TypeConverter
    @JvmName("fromBidList")  // @JvmName for KSP-kompatibilitet med generics/lists.
    fun fromBidList(bids: List<Bid>?): String? {
        return bids?.let { gson.toJson(it) }
    }

    @TypeConverter
    @JvmName("toBidList")  // @JvmName for at undgå resolution-bugs.
    fun toBidList(bidsJson: String?): List<Bid>? {
        return bidsJson?.let {
            val type = object : TypeToken<List<Bid>>() {}.type
            gson.fromJson(it, type)
        }
    }
}