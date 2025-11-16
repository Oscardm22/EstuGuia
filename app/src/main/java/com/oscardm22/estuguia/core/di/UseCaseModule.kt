package com.oscardm22.estuguia.core.di

import com.oscardm22.estuguia.domain.repositories.ScheduleRepository
import com.oscardm22.estuguia.domain.repositories.TaskRepository
import com.oscardm22.estuguia.domain.usecases.schedule.GetSchedulesUseCase
import com.oscardm22.estuguia.domain.usecases.tasks.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideAddTaskUseCase(repository: TaskRepository): AddTaskUseCase {
        return AddTaskUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetTasksUseCase(repository: TaskRepository): GetTasksUseCase {
        return GetTasksUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetTaskByIdUseCase(repository: TaskRepository): GetTaskByIdUseCase {
        return GetTaskByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateTaskUseCase(repository: TaskRepository): UpdateTaskUseCase {
        return UpdateTaskUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteTaskUseCase(repository: TaskRepository): DeleteTaskUseCase {
        return DeleteTaskUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetTasksByScheduleUseCase(repository: TaskRepository): GetTasksByScheduleUseCase {
        return GetTasksByScheduleUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetTasksByStatusUseCase(repository: TaskRepository): GetTasksByStatusUseCase {
        return GetTasksByStatusUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetUpcomingTasksUseCase(repository: TaskRepository): GetUpcomingTasksUseCase {
        return GetUpcomingTasksUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetSchedulesUseCase(repository: ScheduleRepository): GetSchedulesUseCase {
        return GetSchedulesUseCase(repository)
    }
}