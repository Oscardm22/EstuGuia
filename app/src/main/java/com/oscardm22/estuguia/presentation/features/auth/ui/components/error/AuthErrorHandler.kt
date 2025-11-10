package com.oscardm22.estuguia.presentation.features.auth.ui.components.error

import android.content.Context
import com.oscardm22.estuguia.R

/**
 * Maneja todos los errores de autenticación de Firebase
 */
class AuthErrorHandler(private val context: Context) {

    /**
     * Convierte un error técnico de Firebase en un mensaje amigable para el usuario
     */
    fun getFriendlyErrorMessage(error: String?): String {
        return when {
            error == null -> context.getString(R.string.error_unknown)

            // Errores de registro
            containsAny(error, "email address is already in use", "already-in-use") ->
                "Este correo electrónico ya está registrado. ¿Quieres iniciar sesión?"

            // Errores de contraseña
            containsAny(error, "weak password", "password is too weak") ->
                "La contraseña es demasiado débil. Usa al menos 6 caracteres."

            // Errores de email
            containsAny(error, "invalid email", "malformed-email") ->
                "El formato del correo electrónico no es válido."

            // Errores de credenciales (login)
            containsAny(error, "invalid credential", "wrong-password", "user-not-found") ->
                "Email o contraseña incorrectos. Verifica tus datos."

            // Errores de red
            containsAny(error, "network", "socket", "timeout", "unavailable") ->
                "Error de conexión. Verifica tu internet e intenta nuevamente."

            // Errores de cuota/exceso de intentos
            containsAny(error, "too-many-requests", "quota exceeded") ->
                "Demasiados intentos. Espera unos minutos e intenta nuevamente."

            // Errores de usuario deshabilitado
            containsAny(error, "user-disabled") ->
                "Esta cuenta ha sido deshabilitada."

            // Error genérico
            else -> error
        }
    }

    /**
     * Maneja errores específicos de registro
     */
    fun handleRegistrationError(error: String?): String {
        return getFriendlyErrorMessage(error)
    }

    /**
     * Maneja errores específicos de login
     */
    fun handleLoginError(error: String?): String {
        return getFriendlyErrorMessage(error)
    }

    /**
     * Verifica si el error contiene alguna de las palabras clave
     */
    private fun containsAny(error: String, vararg keywords: String): Boolean {
        return keywords.any { keyword ->
            error.contains(keyword, ignoreCase = true)
        }
    }
}