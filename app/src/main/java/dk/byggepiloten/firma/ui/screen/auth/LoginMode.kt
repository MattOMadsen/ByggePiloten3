package dk.byggepiloten.firma.ui.screen.auth

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