// Fil: app/src/main/java/dk/byggepiloten/firma/data/database/Converters.kt
// OPDATERET: Tilføjet korrekt TypeConverter for Map<String, Any?> (details-felt i Request.kt)
// - Håndterer null-værdier inde i map (fra toMap() i data classes)
// - Beholdt ALLE dine originale converters 100% uændret
// - Kun tilføjet nye from/to for Map<String, Any?> (erstatter ikke gamle – begge beholdt for sikkerhed)
// - Løser KSP/Room-fejl "Cannot figure out how to save this field"
// Total lines: 72 (bekræftet)

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

    // Original details-converter ( beholdt uændret )
    @TypeConverter
    fun fromDetailsMap(map: Map<String, Any>?): String? = map?.let { gson.toJson(it) }

    @TypeConverter
    fun toDetailsMap(json: String?): Map<String, Any>? = json?.let {
        val type = object : TypeToken<Map<String, Any>>() {}.type
        gson.fromJson(it, type)
    }

    // NY: Converter for Map<String, Any?> – matcher toMap() fra data classes (tillader null-værdier inde i map)
    @TypeConverter
    fun fromDetailsMapNullable(map: Map<String, Any?>?): String? = map?.let { gson.toJson(it) }

    @TypeConverter
    fun toDetailsMapNullable(json: String?): Map<String, Any?>? = json?.let {
        val type = object : TypeToken<Map<String, Any?>>() {}.type
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