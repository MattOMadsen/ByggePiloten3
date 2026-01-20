// Fil: app/src/main/java/dk/byggepiloten/firma/di/FirebaseModule.kt
// OPDATERET: Kun FirebaseStorage (Firestore allerede provided andetsteds)
// - Fjerner duplicate binding
// - Behold @Singleton
// Total lines: 22

package dk.byggepiloten.firma.di

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return Firebase.storage
    }
}