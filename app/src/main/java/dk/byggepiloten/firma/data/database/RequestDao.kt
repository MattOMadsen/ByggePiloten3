package dk.byggepiloten.firma.data.database

import androidx.room.*
import dk.byggepiloten.firma.data.database.Converters
import dk.byggepiloten.firma.data.model.task.Request

@Dao
interface RequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @TypeConverters(Converters::class)
    suspend fun insertAll(requests: List<Request>)

    @Query("SELECT * FROM requests")
    @TypeConverters(Converters::class)
    suspend fun getAll(): List<Request>

    @Query("DELETE FROM requests WHERE createdAt < :cutoff")
    suspend fun deleteOldRequests(cutoff: Long): Int

    @Query("SELECT * FROM requests WHERE id = :id")
    suspend fun getById(id: String): Request?

    @Query("DELETE FROM requests WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("SELECT * FROM requests WHERE userId = :userId")
    suspend fun getByUserId(userId: String): List<Request>
}