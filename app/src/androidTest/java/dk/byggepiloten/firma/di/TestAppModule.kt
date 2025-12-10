// app/src/androidTest/java/dk/byggepiloten/firma/di/TestAppModule.kt
package dk.byggepiloten.firma.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import dk.byggepiloten.firma.ui.screen.FirebaseIdlingResource

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppProvidesModule::class]  // ERSTAT din produktionsmodule
)
object TestAppModule {

    @Provides
    fun provideIdlingResource(): FirebaseIdlingResource {
        return FirebaseIdlingResource("Test")
    }
}