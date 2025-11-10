package com.oscardm22.estuguia.domain.repositories

import com.oscardm22.estuguia.domain.models.User
import kotlinx.coroutines.flow.Flow

/**
 * Contrato para las operaciones de autenticación en EstuGuía
 * Define qué debe poder hacer el sistema de login/registro
 */
interface AuthRepository {

    // Operaciones de Autenticación

    /**
     * Iniciar sesión con email y contraseña
     * @return Resultado con el usuario autenticado
     */
    suspend fun login(email: String, password: String): Result<User>

    /**
     * Registrar nuevo usuario con email y contraseña
     * @return Resultado con el usuario creado
     */
    suspend fun register(
        email: String,
        password: String,
        name: String,
        grade: String,
        section: String? = null,
        school: String? = null

    ): Result<User>

    /**
     * Cerrar sesión del usuario actual
     */
    suspend fun logout(): Result<Boolean>

    /**
     * Enviar email de recuperación de contraseña
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Boolean>


    // 👥 Estado del Usuario

    /**
     * Flujo que emite el usuario actualmente autenticado
     * Emite null si no hay usuario logueado
     */
    fun getCurrentUser(): Flow<User?>

    /**
     * Verificar si hay un usuario autenticado actualmente
     */
    suspend fun isUserLoggedIn(): Boolean

    /**
     * Obtener el ID del usuario actual
     */
    suspend fun getCurrentUserId(): String?
}