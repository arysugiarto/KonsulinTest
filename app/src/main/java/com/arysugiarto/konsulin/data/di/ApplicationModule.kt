package com.arysugiarto.konsulin.data.di

import android.app.Application
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory
import com.arysugiarto.konsulin.data.preferences.AccessManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [DatabaseModule::class])
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    fun provideApplication(application: Application): Context = application

    @Singleton
    @Provides
    fun provideAccessManager(context: Context) = AccessManager(context)

    @Provides
    @Singleton
    fun provideReviewManager(@ApplicationContext context: Context) =
        ReviewManagerFactory.create(context)



}