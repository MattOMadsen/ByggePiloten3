package dk.byggepiloten.firma.data.repository

import dk.byggepiloten.firma.data.model.Request
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
}