package com.oscardm22.estuguia.core.di

import com.oscardm22.estuguia.data.repositories.AuthRepositoryImpl
import com.oscardm22.estuguia.data.datasources.remote.FirebaseAuthDataSource
import com.oscardm22.estuguia.domain.repositories.AuthRepository
import com.oscardm22.estuguia.domain.usecases.auth.LoginUseCase
import dagger.Module
import dagger.Provides
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.oscardm22.estuguia.domain.usecases.auth.DeleteAccountUseCase
import com.oscardm22.estuguia.domain.usecases.auth.GetCurrentUserProfileUseCase
import com.oscardm22.estuguia.domain.usecases.auth.LogoutUseCase
import com.oscardm22.estuguia.domain.usecases.auth.UpdatePasswordUseCase
import com.oscardm22.estuguia.domain.usecases.auth.UpdateProfileUseCase
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    // Data Sources
    @Provides
    @Singleton
    fun provideFirebaseAuthDataSource(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): FirebaseAuthDataSource {
        return FirebaseAuthDataSource(firebaseAuth, firestore)
    }

    // Repositories
    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuthDataSource: FirebaseAuthDataSource
    ): AuthRepository {
        return AuthRepositoryImpl(firebaseAuthDataSource)
    }

    // Use Cases
    @Provides
    @Singleton
    fun provideLoginUseCase(
        authRepository: AuthRepository
    ): LoginUseCase {
        return LoginUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideGetCurrentUserProfileUseCase(
        authRepository: AuthRepository
    ): GetCurrentUserProfileUseCase {
        return GetCurrentUserProfileUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateProfileUseCase(
        authRepository: AuthRepository
    ): UpdateProfileUseCase {
        return UpdateProfileUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideUpdatePasswordUseCase(
        authRepository: AuthRepository
    ): UpdatePasswordUseCase {
        return UpdatePasswordUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteAccountUseCase(
        authRepository: AuthRepository
    ): DeleteAccountUseCase {
        return DeleteAccountUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideLogoutUseCase(
        authRepository: AuthRepository
    ): LogoutUseCase {
        return LogoutUseCase(authRepository)
    }
}