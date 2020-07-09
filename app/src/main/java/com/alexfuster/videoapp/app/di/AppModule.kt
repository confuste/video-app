package com.alexfuster.videoapp.app.di

import com.alexfuster.videoapp.app.constants.AppConfig
import com.alexfuster.videoapp.domain.repository.ExternalStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideExternalStorage(): ExternalStorage = ExternalStorage(AppConfig.EXTERNAL_APP_FOLDER)


}