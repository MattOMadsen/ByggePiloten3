package dk.byggepiloten.firma.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import dk.byggepiloten.firma.data.database.BackupDao
import dk.byggepiloten.firma.data.database.FirmaMaterialDao
import dk.byggepiloten.firma.data.database.RequestDao
import dk.byggepiloten.firma.data.database.UserDao
import dk.byggepiloten.firma.data.database.Converters  // BEHOLDT: Import for type converters (matcher din upload)
import dk.byggepiloten.firma.data.model.FirmaUser  // BEHOLDT: Import fra data.model (matcher din upload – brug kun FirmaUser for "users"-table)
import dk.byggepiloten.firma.data.model.Request  // BEHOLDT: Import fra data.model (matcher struktur – antager @Entity i Request.kt)
import dk.byggepiloten.firma.data.model.FirmaMaterialPrice  // BEHOLDT: Import fra data.model (matcher struktur – antager @Entity i FirmaMaterialPrice.kt)
import dk.byggepiloten.firma.data.model.BackupInfo  // BEHOLDT: Import fra data.model (matcher struktur – antager @Entity i BackupInfo.kt)

@Database(
    entities = [FirmaUser::class, Request::class, FirmaMaterialPrice::class, BackupInfo::class],  // BEHOLDT: Kun FirmaUser for "users" (løser multiple entities-fejl); behold korrekte modeller fra data.model
    version = 2,
    exportSchema = true  // BEHOLDT: True for KSP-resolve af MissingType (tillader scan af entities/DAOs; matcher Room incremental med KSP 2.0.20-1.0.25)
)
@TypeConverters(Converters::class)  // BEHOLDT: Tilføjet for global type converters (List<String> i Request/BackupInfo – matcher din Converters.kt)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun requestDao(): RequestDao
    abstract fun firmaMaterialDao(): FirmaMaterialDao
    abstract fun backupDao(): BackupDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "byggepiloten_database"
                )
                    .fallbackToDestructiveMigration()  // BEHOLDT: Uden args (standard – løser too many arguments fra tidligere)
                    .build()
                INSTANCE = instance
                // Add the callback here if needed
                instance
            }
        }
    }
}