// File: app/src/main/java/dk/byggepiloten/firma/data/database/AppDatabase.kt
package dk.byggepiloten.firma.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration
import dk.byggepiloten.firma.data.model.user.FirmaUser
import dk.byggepiloten.firma.data.model.task.Request
import dk.byggepiloten.firma.data.model.price.FirmaMaterialPrice
import dk.byggepiloten.firma.data.misc.BackupInfo

@Database(
    entities = [FirmaUser::class, Request::class, FirmaMaterialPrice::class, BackupInfo::class],
    version = 5,
    exportSchema = true
)
@TypeConverters(Converters::class)
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
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // Dette sletter databasen ved ændringer (godt til udvikling)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
