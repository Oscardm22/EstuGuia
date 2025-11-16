package com.oscardm22.estuguia.core.di

import com.oscardm22.estuguia.data.repositories.ScheduleRepositoryImpl
import com.oscardm22.estuguia.data.repositories.TaskRepositoryImpl
import com.oscardm22.estuguia.domain.repositories.ScheduleRepository
import com.oscardm22.estuguia.domain.repositories.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindScheduleRepository(
        scheduleRepositoryImpl: ScheduleRepositoryImpl
    ): ScheduleRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository
}