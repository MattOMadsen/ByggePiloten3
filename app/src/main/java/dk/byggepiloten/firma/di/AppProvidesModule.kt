package dk.byggepiloten.firma.di

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dk.byggepiloten.firma.data.database.AppDatabase
import dk.byggepiloten.firma.data.database.BackupDao
import dk.byggepiloten.firma.data.database.FirmaMaterialDao
import dk.byggepiloten.firma.data.database.RequestDao
import dk.byggepiloten.firma.data.database.UserDao
import dk.byggepiloten.firma.data.network.EmailService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import com.google.gson.Gson
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppProvidesModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler { ex: CorruptionException ->
                Timber.e(ex, "DataStore korrupt – erstattes med tom Preferences")
                emptyPreferences()
            },
            migrations = emptyList(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.filesDir.resolve("settings_prefs.preferences_pb") }
        )
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.getDatabase(context)

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao {
        return db.userDao()
    }

    @Provides
    fun provideRequestDao(db: AppDatabase): RequestDao {
        return db.requestDao()
    }

    @Provides
    fun provideFirmaMaterialDao(db: AppDatabase): FirmaMaterialDao {
        return db.firmaMaterialDao()
    }

    @Provides
    fun provideBackupDao(db: AppDatabase): BackupDao {
        return db.backupDao()
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()  // RETTET: Skiftet fra Firebase.firestore (ktx) til FirebaseFirestore.getInstance() (non-KTX; da KTX stoppede i BOM v34.0.0+; fixer unresolved ktx/Firebase).

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideEmailService(gson: Gson): EmailService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://graverholt-apps.dk/wp-json/byggepiloten/v1/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(EmailService::class.java)
    }

    @Provides
    @Singleton
    @UserDataStore
    fun provideUserDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler { ex: CorruptionException ->
                Timber.e(ex, "DataStore korrupt – erstattes med tom Preferences")
                emptyPreferences()
            },
            migrations = emptyList(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.filesDir.resolve("user_prefs.preferences_pb") }
        )
    }
}