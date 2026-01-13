// Fil: app/src/main/java/dk/byggepiloten/firma/data/repository/RequestRepository.kt
// OPDATERET: Tilføjet suspend fun updateRequest(request: Request) til interface.
// Trin-for-trin forklaring:
// 1. Beholdt 100% af din originale interface (alle eksisterende metoder).
// 2. TILFØJET: suspend fun updateRequest(request: Request) – for reel update af request (bids, status osv.) i Firestore + Room.
// 3. Fuldt funktionsdygtig – matcher din impl (vi opdaterer impl i næste fil).
// 4. Ingen andre ændringer – kun tilføjelse for at løse unresolved 'updateRequest' i ViewModels.

package dk.byggepiloten.firma.data.repository

import dk.byggepiloten.firma.data.model.task.Request
import kotlinx.coroutines.flow.Flow

interface RequestRepository {

    fun getAllRequests(): Flow<List<Request>>

    suspend fun createRequest(request: Request)

    suspend fun syncRequests()

    suspend fun deleteOldRequests(): Int

    suspend fun getRequestById(id: String): Request?

    suspend fun createTestRequest(
        category: String,
        areaM2: Float,
        roomType: String,
        aiPrice: Float
    )

    suspend fun getUserRequests(): List<Request>?

    suspend fun deleteRequest(requestId: String)

    // NY: Reel update af request (tilføj bids, opdater status osv.)
    suspend fun updateRequest(request: Request)
}