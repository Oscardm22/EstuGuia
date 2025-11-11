package com.oscardm22.estuguia.core.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.app.ActivityOptionsCompat
import com.oscardm22.estuguia.presentation.features.auth.ui.activities.ForgotPasswordActivity
import com.oscardm22.estuguia.presentation.features.auth.ui.activities.LoginActivity
import com.oscardm22.estuguia.presentation.features.auth.ui.activities.RegisterActivity
import com.oscardm22.estuguia.presentation.features.main.ui.activities.MainActivity

/**
 * Maneja toda la navegación entre pantallas de autenticación
 * Centraliza intents, flags y transiciones
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

        if (!clearTask && context is Activity) {
            context.finish()
        }
    }

    /**
     * Navega a la pantalla de Registro
     */
    fun navigateToRegister() {
        val intent = Intent(context, RegisterActivity::class.java)

        if (context is Activity) {
            val options = ActivityOptionsCompat.makeCustomAnimation(
                context,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            ).toBundle()
            context.startActivity(intent, options)
        } else {
            context.startActivity(intent)
        }
    }

    /**
     * Navega a la pantalla principal de la app (después del login exitoso)
     */
    fun navigateToMain() {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

        if (context is Activity) {
            val options = ActivityOptionsCompat.makeCustomAnimation(
                context,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            ).toBundle()
            context.startActivity(intent, options)
        } else {
            context.startActivity(intent)
        }
    }

    /**
     * Navega a la pantalla de recuperación de contraseña
     */
    fun navigateToForgotPassword() {
        val intent = Intent(context, ForgotPasswordActivity::class.java)

        if (context is Activity) {
            val options = ActivityOptionsCompat.makeCustomAnimation(
                context,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            ).toBundle()
            context.startActivity(intent, options)
        } else {
            context.startActivity(intent)
        }
    }

    /**
     * Cierra la actividad actual
     */
    fun finishCurrentActivity() {
        if (context is Activity) {
            context.finish()
        }
    }

    /**
     * Navega hacia atrás
     */
    fun navigateBack() {
        if (context is Activity) {
            context.finish()
        }
    }
}