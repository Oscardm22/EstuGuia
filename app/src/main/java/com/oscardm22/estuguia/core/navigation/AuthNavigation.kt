package com.oscardm22.estuguia.core.navigation

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.oscardm22.estuguia.R
import com.oscardm22.estuguia.presentation.features.auth.ui.activities.LoginActivity
import com.oscardm22.estuguia.presentation.features.auth.ui.activities.RegisterActivity

/**
 * Maneja toda la navegación entre pantallas de autenticación
 * Centraliza intents, flags y transiciones
 * Ubicado en core/navigation porque la navegación es transversal a múltiples features
 */
class AuthNavigation(private val context: Context) {

    /**
     * Navega a la pantalla de Login
     * @param clearTask Si es true, limpia el stack de actividades
     */
    fun navigateToLogin(clearTask: Boolean = false) {
        val intent = Intent(context, LoginActivity::class.java)

        if (clearTask) {
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(intent)

        // Si clearTask es true, no llamamos finish() porque estamos empezando una nueva tarea
        if (!clearTask && context is android.app.Activity) {
            context.finish()
        }
    }

    /**
     * Navega a la pantalla de Registro
     */
    fun navigateToRegister() {
        val intent = Intent(context, RegisterActivity::class.java)
        context.startActivity(intent)

        // Opcional: agregar animación de transición
        if (context is android.app.Activity) {
            // context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    /**
     * Navega a la pantalla principal de la app (después del login exitoso)
     * TODO: Reemplazar con MainActivity cuando esté implementada
     */
    fun navigateToMain() {
        // Por ahora navega al Login como placeholder
        navigateToLogin(clearTask = true)

        // Mostrar mensaje de éxito
        Toast.makeText(context, R.string.login_success, Toast.LENGTH_SHORT).show()

        // TODO: Implementar navegación real a MainActivity
        // val intent = Intent(context, MainActivity::class.java)
        // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        // context.startActivity(intent)
    }

    /**
     * Navega a la pantalla de recuperación de contraseña
     * TODO: Implementar cuando se cree ForgotPasswordActivity
     */
    fun navigateToForgotPassword() {
        // Placeholder por ahora
        Toast.makeText(context, R.string.navigate_forgot_password, Toast.LENGTH_SHORT).show()

        // TODO: Implementar navegación real
        // val intent = Intent(context, ForgotPasswordActivity::class.java)
        // context.startActivity(intent)
    }

    /**
     * Cierra la actividad actual
     */
    fun finishCurrentActivity() {
        if (context is android.app.Activity) {
            context.finish()
        }
    }

    /**
     * Navega hacia atrás
     */
    fun navigateBack() {
        if (context is android.app.Activity) {
            context.finish()
        }
    }
}