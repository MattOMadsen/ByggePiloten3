// File: app/src/main/java/dk/byggepiloten/firma/data/repository/PriceRepository.kt
package dk.byggepiloten.firma.data.repository

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceRepository @Inject constructor() {

    // Simpel flag – i rigtig app bruger vi DataStore
    private var hasSavedPrices = false

    fun hasSavedPrices(): Boolean = hasSavedPrices

    fun markPricesSaved() {
        hasSavedPrices = true
    }
}