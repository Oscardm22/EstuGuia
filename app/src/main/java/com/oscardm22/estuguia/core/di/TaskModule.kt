package com.oscardm22.estuguia.core.di

import com.google.firebase.firestore.FirebaseFirestore
import com.oscardm22.estuguia.data.datasources.remote.FirestoreTaskDataSource
import com.oscardm22.estuguia.data.repositories.TaskRepositoryImpl
import com.oscardm22.estuguia.domain.repositories.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TaskModule {

    @Provides
    @Singleton
    fun provideFirestoreTaskDataSource(
        firestore: FirebaseFirestore
    ): FirestoreTaskDataSource {
        return FirestoreTaskDataSource(firestore)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(
        firestoreTaskDataSource: FirestoreTaskDataSource
    ): TaskRepository {
        return TaskRepositoryImpl(firestoreTaskDataSource)
    }
}