package com.oscardm22.estuguia.core.di

import com.oscardm22.estuguia.data.repositories.AuthRepositoryImpl
import com.oscardm22.estuguia.data.datasources.remote.FirebaseAuthDataSource
import com.oscardm22.estuguia.domain.repositories.AuthRepository
import com.oscardm22.estuguia.domain.usecases.LoginUseCase
import dagger.Module
import dagger.Provides
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    // 🔥 Firebase Providers
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    // 📡 Data Sources
    @Provides
    @Singleton
    fun provideFirebaseAuthDataSource(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): FirebaseAuthDataSource {
        return FirebaseAuthDataSource(firebaseAuth, firestore)
    }

    // 📚 Repositories
    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuthDataSource: FirebaseAuthDataSource
    ): AuthRepository {
        return AuthRepositoryImpl(firebaseAuthDataSource)
    }

    // 🎯 Use Cases
    @Provides
    @Singleton
    fun provideLoginUseCase(
        authRepository: AuthRepository
    ): LoginUseCase {
        return LoginUseCase(authRepository)
    }
}