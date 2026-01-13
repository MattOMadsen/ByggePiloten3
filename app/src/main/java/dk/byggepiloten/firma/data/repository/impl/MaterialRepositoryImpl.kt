// File: data/repository/impl/MaterialRepositoryImpl.kt
package dk.byggepiloten.firma.data.repository.impl

import dk.byggepiloten.firma.data.model.price.MaterialEstimate
import dk.byggepiloten.firma.data.repository.MaterialRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MaterialRepositoryImpl @Inject constructor() : MaterialRepository {

    // Ingen MaterialDao – vi bruger kun fallback indtil videre
    // Ingen Gson @Inject – vi opretter den lokalt

    override suspend fun estimatePrice(areaM2: Float, roomType: String, category: String): MaterialEstimate {
        return getFallbackEstimate(category, areaM2)
    }

    override fun getFallbackPrices(): Flow<Map<String, Float>> = flow {
        val prices = mapOf(
            "Opmuring" to 650f,
            "Badeværelse" to 500f,
            "Vandskuring" to 400f,
            "Puds" to 350f,
            "KlinkerFliser" to 675f,
            "Facadeisolering" to 800f,
            "StøbeNytGulv" to 350f,
            "Omfugning" to 450f,
            "Andet" to 550f
        )
        emit(prices)
    }.flowOn(Dispatchers.IO)

    private fun getFallbackEstimate(category: String, areaM2: Float): MaterialEstimate {
        val basePrice = when (category) {
            "Opmuring" -> 650f
            "Badeværelse" -> 500f
            "Vandskuring" -> 400f
            "Puds" -> 350f
            "KlinkerFliser" -> 675f
            "Facadeisolering" -> 800f
            "StøbeNytGulv" -> 350f
            "Omfugning" -> 450f
            else -> 550f
        }

        val totalPrice = basePrice * areaM2
        val estimatedDays = (areaM2 / 10f).coerceAtLeast(1f).toInt()

        val materials = when (category) {
            "Opmuring" -> mapOf(
                "Cement" to Pair(areaM2 * 0.5f, 100f),
                "Mursten" to Pair(areaM2 * 50f, 2f)
            )
            "Badeværelse" -> mapOf(
                "Membrane" to Pair(areaM2, 200f),
                "Fliser" to Pair(areaM2 * 10f, 5f)
            )
            else -> emptyMap()
        }

        return MaterialEstimate(
            totalPrice = totalPrice,
            materials = materials,
            estimatedDays = estimatedDays
        )
    }
}