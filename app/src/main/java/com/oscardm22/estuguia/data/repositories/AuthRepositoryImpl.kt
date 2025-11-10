package com.oscardm22.estuguia.data.repositories

import com.oscardm22.estuguia.data.model.UserDto
import com.oscardm22.estuguia.data.datasources.remote.FirebaseAuthDataSource
import com.oscardm22.estuguia.domain.models.User
import com.oscardm22.estuguia.domain.repositories.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val userDto = firebaseAuthDataSource.signInWithEmailAndPassword(email, password)
            Result.success(userDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        name: String,
        grade: String,
        section: String?,
        school: String?
    ): Result<User> {
        return try {
            val userDto = firebaseAuthDataSource.createUserWithEmailAndPassword(
                email = email,
                password = password,
                name = name,
                grade = grade,
                section = section,
                school = null
            )
            Result.success(userDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Boolean> {
        return try {
            firebaseAuthDataSource.signOut()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Boolean> {
        return try {
            firebaseAuthDataSource.sendPasswordResetEmail(email)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): Flow<User?> {
        return firebaseAuthDataSource.getCurrentUser().map { userDto ->
            userDto?.toDomain()
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return firebaseAuthDataSource.getCurrentUserSync() != null
    }

    override suspend fun getCurrentUserId(): String? {
        return firebaseAuthDataSource.getCurrentUserSync()?.id
    }
}

// Extensión para convertir DTO a Domain
private fun UserDto.toDomain(): User {
    return User(
        id = this.id,
        email = this.email,
        name = this.name,
        grade = this.grade,
        section = this.section,
        school = this.school,
        profileImage = this.profileImage,
        createdAt = this.createdAt,
        isEmailVerified = this.isEmailVerified
    )
}