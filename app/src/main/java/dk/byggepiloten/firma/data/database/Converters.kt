// File: app/src/main/java/dk/byggepiloten/firma/data/database/Converters.kt
package dk.byggepiloten.firma.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dk.byggepiloten.firma.data.model.price.FirmaMaterialPrice
import dk.byggepiloten.firma.data.model.task.Bid

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromFirmaMaterialPriceList(prices: List<FirmaMaterialPrice>?): String? = prices?.let { gson.toJson(it) }

    @TypeConverter
    fun toFirmaMaterialPriceList(json: String?): List<FirmaMaterialPrice>? = json?.let {
        val type = object : TypeToken<List<FirmaMaterialPrice>>() {}.type
        gson.fromJson(it, type)
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String? = list?.let { gson.toJson(it) }

    @TypeConverter
    fun toStringList(json: String?): List<String>? = json?.let {
        val type = object : TypeToken<List<String>>() {}.type
        gson.fromJson(it, type)
    }

    @TypeConverter
    fun fromBidList(list: List<Bid>?): String? = list?.let { gson.toJson(it) }

    @TypeConverter
    fun toBidList(json: String?): List<Bid>? = json?.let {
        val type = object : TypeToken<List<Bid>>() {}.type
        gson.fromJson(it, type)
    }

    @TypeConverter
    fun fromDetailsMap(map: Map<String, Any>?): String? = map?.let { gson.toJson(it) }

    @TypeConverter
    fun toDetailsMap(json: String?): Map<String, Any>? = json?.let {
        val type = object : TypeToken<Map<String, Any>>() {}.type
        gson.fromJson(it, type)
    }

    @TypeConverter
    fun fromLabeledPhotosMap(map: Map<String, List<String>>?): String? = map?.let { gson.toJson(it) }

    @TypeConverter
    fun toLabeledPhotosMap(json: String?): Map<String, List<String>>? = json?.let {
        val type = object : TypeToken<Map<String, List<String>>>() {}.type
        gson.fromJson(it, type)
    }
}
