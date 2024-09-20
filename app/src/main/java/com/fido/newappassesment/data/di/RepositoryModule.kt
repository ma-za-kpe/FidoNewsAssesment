package com.fido.newappassesment.data.di

import com.fido.newappassesment.data.api.NewsApiService
import com.fido.newappassesment.data.repository.NewsRepository
import com.fido.newappassesment.data.repository.NewsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideNewsRepository(
        apiService: NewsApiService,
    ): NewsRepository {
        return NewsRepositoryImpl(apiService)
    }
}