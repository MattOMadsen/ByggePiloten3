// File: app/src/main/java/dk/byggepiloten/firma/ui/viewmodel/LoginMode.kt
// FULD, KOMPLET, KØRBAR – GENDA NET ENUM (ingen ændringer nødvendige – beholdt originalt).
// Trin-for-trin forklaring:
// 1. Beholdt ALLE originale elementer uændret (ingen sletninger – beholdt enum PASSWORD, LINK og KDoc-kommentar).
// 2. Flyttet til separat fil (som du havde) – løser unresolved reference i LoginScreen.
// 3. Fuldt funktionsdygtig – brug i LoginScreen for toggle (RadioButton selected = loginMode == LoginMode.LINK).
// 4. Matcher ViewModel og UI (PASSWORD: vis password-felt; LINK: kun email + sendMagicLink).
// 5. Efter opdatering: Sync Gradle – ingen fejl, enum importeres korrekt.
// Note: Per plan – passwordless fallback for sikkerhed (Magic Link).

package dk.byggepiloten.firma.ui.viewmodel

/**
 * LoginMode: Enum for login-typer (password vs. passwordless link).
 *
 * Bruges i LoginViewModel og LoginScreen for toggle mellem modes.
 *
 * FIXED: Flyttet fra companion object i LoginViewModel til separat fil for klarhed og import (løser unresolved reference i screens).
 *
 * Trin 1: PASSWORD for standard email/password.
 * Trin 2: LINK for passwordless (Firebase signInWithEmailLink, matcher deep link i Manifest).
 *
 * Integration: Toggle via RadioButton i UI; guards i onLoginClick (kræver verified + GDPR).
 *
 * Test: Sæt mode → check UI updates (vis/skjul password-felt).
 *
 * Note: Matcher oversigt (sektion 2): Passwordless fallback for sikkerhed.
 */
enum class LoginMode {
    PASSWORD,
    LINK
}