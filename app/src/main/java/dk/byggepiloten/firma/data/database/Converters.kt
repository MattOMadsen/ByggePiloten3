// File: app/src/main/java/dk/byggepiloten/firma/data/database/Converters.kt
package dk.byggepiloten.firma.data.database  // RETTET: Flyttet til data.database for bedre KSP resolution (fixer subtype-fejl i Room-processor).

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dk.byggepiloten.firma.data.model.FirmaMaterialPrice

class Converters {
    private val gson = Gson()

    @TypeConverter
    @JvmName("fromFirmaMaterialPriceList")  // TILFØJET: @JvmName for bedre KSP compatibility med generics/lists (fra KSP issues).
    fun fromFirmaMaterialPriceList(prices: List<FirmaMaterialPrice>?): String? {
        return prices?.let { gson.toJson(it) }
    }

    @TypeConverter
    @JvmName("toFirmaMaterialPriceList")  // TILFØJET: @JvmName for at undgå resolution-bugs.
    fun toFirmaMaterialPriceList(pricesJson: String?): List<FirmaMaterialPrice>? {
        return pricesJson?.let {
            val type = object : TypeToken<List<FirmaMaterialPrice>>() {}.type
            gson.fromJson(it, type)
        }
    }

    @TypeConverter
    @JvmName("fromStringList")  // TILFØJET: @JvmName for lists.
    fun fromStringList(images: List<String>?): String? {
        return images?.let { gson.toJson(it) }
    }

    @TypeConverter
    @JvmName("toStringList")  // TILFØJET: @JvmName.
    fun toStringList(imagesJson: String?): List<String>? {
        return imagesJson?.let {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(it, type)
        }
    }
}