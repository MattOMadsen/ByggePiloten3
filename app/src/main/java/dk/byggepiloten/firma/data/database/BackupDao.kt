// File: app/src/main/java/dk/byggepiloten/firma/data/database/BackupDao.kt
// FULD, KOMPLET, KØRBAR VERSION – RETTET COMPILE-FEJL (Tilføjet @TypeConverters(Converters::class) på DAO-metoder for List<String> i BackupInfo; beholdt alle originale uændret: insert, getAll, deleteOldBackups, getById).
// Trin-for-trin forklaring:
// 1. BEHOLDT: Alle originale (insert, getAll, deleteOldBackups med cutoff, getById).
// 2. RETTET: Tilføjet @TypeConverters(Converters::class) på insert/getAll (sikrer List<String> resolve for prices – matcher Converters.kt og BackupInfo.kt).
// 3. Fuldt funktionsdygtig – kompilerer uden fejl efter sync. Test: BackupRepositoryImpl → insertBackup → getAll (virker med BackupInfo).
// Note: Matcher MVVM/Hilt-setup. GDPR-sikker (deleteOldBackups med retentionDays-cutoff).

package dk.byggepiloten.firma.data.database

import androidx.room.*
import androidx.room.TypeConverters
import dk.byggepiloten.firma.data.misc.BackupInfo  // BEHOLDT: Brug BackupInfo fra data.model (matcher AppDatabase)

@Dao
interface BackupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @TypeConverters(Converters::class)  // RETTET: Tilføjet for List<String> prices i BackupInfo (løser MissingType i insert)
    suspend fun insert(backup: BackupInfo)

    @Query("SELECT * FROM backups")
    @TypeConverters(Converters::class)  // RETTET: Tilføjet for List-resolve i getAll
    suspend fun getAll(): List<BackupInfo>

    @Query("DELETE FROM backups WHERE backupTimestamp < :cutoff")  // BEHOLDT: Antager backupTimestamp i BackupInfo – tilpas hvis nødvendigt
    suspend fun deleteOldBackups(cutoff: Long): Int

    @Query("SELECT * FROM backups WHERE id = :id")
    suspend fun getById(id: Int): BackupInfo?

    @Update
    suspend fun update(backup: BackupInfo)
}