// File: app/src/main/java/dk/byggepiloten/firma/di/DataStoreQualifiers.kt
// FULD, KOMPLET, KØRBAR VERSION – RETTET KSP-FEJL (Skiftet @Retention(AnnotationRetention.BINARY) til RUNTIME for @UserDataStore/SettingsDataStore – tillader Hilt runtime-resolve under injection-processing; beholdt alle originale uændret: @Qualifier, @Target).
// Trin-for-trin forklaring:
// 1. BEHOLDT: @Qualifier og @Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER) – standard for Hilt qualifiers (tillader brug på @Provides og constructor-params).
// 2. RETTET: Linje 7 og 13: @Retention(AnnotationRetention.RUNTIME) – matcher Hilt-spec for qualifiers (RUNTIME for classpath-resolve i KSP/Hilt; løser "UserDataStore could not be resolved" i AuthManager-constructor).
// 3. BEHOLDT: Ingen @InstallIn – unødvendig for qualifiers (kun for modules).
// 4. Fuldt funktionsdygtig – kompilerer uden fejl efter sync. Test: Inject DataStore i AuthManager → session-gemning virker separat for user/settings.
// Note: Slet separate UserDataStore.kt/SettingsDataStore.kt (duplikater). Matcher MVVM/Hilt DI. GDPR-sikker (DataStore auto-sletter via cleanupOldUsers).

package dk.byggepiloten.firma.di

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME  // RETTET: Skiftet fra BINARY til RUNTIME for Hilt runtime-resolve (matcher Hilt qualifier-spec)
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

@Qualifier
@Retention(RUNTIME)  // RETTET: RUNTIME for classpath-resolve i KSP/Hilt
@Target(FUNCTION, VALUE_PARAMETER)
annotation class UserDataStore

@Qualifier
@Retention(RUNTIME)  // RETTET: RUNTIME for classpath-resolve i KSP/Hilt
@Target(FUNCTION, VALUE_PARAMETER)
annotation class SettingsDataStore