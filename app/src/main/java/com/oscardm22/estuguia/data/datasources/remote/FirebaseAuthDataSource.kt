package com.oscardm22.estuguia.data.datasources.remote

import com.oscardm22.estuguia.data.model.UserDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun signInWithEmailAndPassword(email: String, password: String): UserDto {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("Error en autenticación")

        // Obtener datos adicionales de Firestore
        return getUserFromFirestore(firebaseUser.uid)
    }

    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        name: String,
        grade: String,
        section: String?,
        school: String?
    ): UserDto {
        // 1. Crear usuario en Firebase Auth
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("Error creando usuario")

        // 2. Actualizar perfil con nombre
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        firebaseUser.updateProfile(profileUpdates).await()

        // 3. Guardar datos adicionales en Firestore
        val userDto = UserDto(
            id = firebaseUser.uid,
            email = email,
            name = name,
            grade = grade,
            section = section,
            school = school,
            profileImage = null,
            createdAt = System.currentTimeMillis(),
            isEmailVerified = false
        )

        saveUserToFirestore(userDto)
        return userDto
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    suspend fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    fun getCurrentUser(): Flow<UserDto?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                try {
                    val basicUserDto = UserDto(
                        id = user.uid,
                        email = user.email ?: "",
                        name = user.displayName ?: "",
                        grade = "",
                        section = null,
                        school = null,
                        profileImage = user.photoUrl?.toString(),
                        createdAt = 0,
                        isEmailVerified = user.isEmailVerified
                    )
                    trySend(basicUserDto)
                } catch (e: Exception) {
                    trySend(null)
                }
            } else {
                trySend(null)
            }
        }

        // Registrar el listener
        firebaseAuth.addAuthStateListener(authStateListener)

        // Limpiar cuando el flow se cancele
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

    fun getCurrentUserSync(): UserDto? {
        val firebaseUser = firebaseAuth.currentUser
        return firebaseUser?.let { user ->
            try {
                UserDto(
                    id = user.uid,
                    email = user.email ?: "",
                    name = user.displayName ?: "",
                    grade = "",
                    section = null,
                    school = null,
                    profileImage = user.photoUrl?.toString(),
                    createdAt = 0,
                    isEmailVerified = user.isEmailVerified
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun getCurrentUserProfile(): UserDto {
        val currentUser = firebaseAuth.currentUser
            ?: throw Exception("No hay usuario autenticado")

        return getUserFromFirestore(currentUser.uid)
    }

    suspend fun updateProfile(userDto: UserDto): Boolean {
        return try {
            // 1. Actualizar en Firestore
            firestore.collection("users")
                .document(userDto.id)
                .set(userDto)
                .await()

            // 2. Actualizar en Firebase Auth (solo nombre)
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(userDto.name)
                    .build()
                currentUser.updateProfile(profileUpdates).await()
            }

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updatePassword(currentPassword: String, newPassword: String): Boolean {
        return try {
            val user = firebaseAuth.currentUser
                ?: throw Exception("No hay usuario autenticado")

            // Reautenticar antes de cambiar contraseña
            val credential = com.google.firebase.auth.EmailAuthProvider
                .getCredential(user.email ?: "", currentPassword)

            user.reauthenticate(credential).await()
            user.updatePassword(newPassword).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteAccount(): Boolean {
        return try {
            val user = firebaseAuth.currentUser
                ?: throw Exception("No hay usuario autenticado")

            // 1. Eliminar de Firestore
            firestore.collection("users")
                .document(user.uid)
                .delete()
                .await()

            // 2. Eliminar de Firebase Auth
            user.delete().await()

            // 3. CERRAR SESIÓN
            firebaseAuth.signOut()

            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun getUserFromFirestore(userId: String): UserDto {
        val document = firestore.collection("users").document(userId).get().await()
        return document.toObject(UserDto::class.java) ?: throw Exception("Usuario no encontrado en Firestore")
    }

    private suspend fun saveUserToFirestore(userDto: UserDto) {
        firestore.collection("users")
            .document(userDto.id)
            .set(userDto)
            .await()
    }
}