package com.example.frnd_task.di

import com.example.frnd_task.repository.CalendarApi
import com.example.frnd_task.repository.CalendarRepository
import com.example.frnd_task.repository.CalendarRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "http://dev.frndapp.in:8085/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCalendarApi(retrofit: Retrofit): CalendarApi {
        return retrofit.create(CalendarApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCalendarRepository(calendarApi: CalendarApi): CalendarRepository {
        return CalendarRepositoryImpl(calendarApi)
    }
}