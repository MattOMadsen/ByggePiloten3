package dk.byggepiloten.firma.data.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import dk.byggepiloten.firma.data.database.RequestDao
import dk.byggepiloten.firma.data.model.Request
import dk.byggepiloten.firma.data.repository.RequestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestRepositoryImpl @Inject constructor(
    private val requestDao: RequestDao,
    private val firestore: FirebaseFirestore
) : RequestRepository {

    private val requestsCollection = firestore.collection("requests")

    override fun getAllRequests(): Flow<List<Request>> = flow {
        try {
            val snapshot = requestsCollection.get().await()
            val requests = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Request::class.java)  // RETTET: Skiftet fra ktx toObject<Request>() til non-KTX toObject(Class) (da KTX stoppede i BOM v34.0.0+; fixer unresolved ktx).
            }
            if (requests.isNotEmpty()) {
                requestDao.insertAll(requests)
            }
            Timber.d("Firestore: Hentede ${requests.size} opgaver")
            emit(requests)
        } catch (e: Exception) {
            Timber.e(e, "Firestore fejl – bruger lokal cache")
            emit(requestDao.getAll())  // RETTET: Fjernet .first() – nu emitter direkte List<Request> (løser type mismatch)
        }
    }

    override suspend fun createRequest(request: Request) {
        try {
            requestsCollection.document(request.id).set(request).await()
            requestDao.insertAll(listOf(request))
            Timber.d("Opgave oprettet i Firestore + lokal")
        } catch (e: Exception) {
            Timber.e(e, "Kunne ikke oprette i Firestore – gemmer lokalt")
            requestDao.insertAll(listOf(request))
        }
    }

    override suspend fun syncRequests() {
        // TODO senere
    }

    override suspend fun deleteOldRequests(): Int {
        val cutoff = System.currentTimeMillis() - 24 * 60 * 60 * 1000
        return requestDao.deleteOldRequests(cutoff)
    }

    override suspend fun getRequestById(id: String): Request? {
        return requestDao.getById(id)
    }

    override suspend fun createTestRequest(category: String, areaM2: Float, roomType: String, aiPrice: Float) {
        val testRequest = Request(
            userId = "test_user_123",
            role = "private",
            fag = "Murer",
            category = category,
            areaM2 = areaM2,
            roomType = roomType,
            requiresMembrane = true,
            aiPrice = aiPrice,
            images = emptyList(),
            sentAt = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis()
        )
        createRequest(testRequest)
    }

    override suspend fun getUserRequests(): List<Request>? {
        val userId = "current_user_id" // Placeholder
        return requestDao.getByUserId(userId)
    }

    override suspend fun deleteRequest(requestId: String) {
        try {
            requestsCollection.document(requestId).delete().await()
            requestDao.deleteById(requestId)
            Timber.d("Request slettet: $requestId")
        } catch (e: Exception) {
            Timber.e(e, "Delete request fejl: $requestId")
        }
    }
}