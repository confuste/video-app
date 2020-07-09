package com.alexfuster.videoapp.app.di

import com.alexfuster.videoapp.app.constants.AppConfig
import com.alexfuster.videoapp.domain.repository.ExternalStorage
import com.alexfuster.videoapp.ui.gallery.GalleryViewModel
import com.alexfuster.videoapp.ui.recorder.RecorderViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
class ActivityModule {

    @Provides
    fun provideGalleryViewModel() = GalleryViewModel(ExternalStorage(AppConfig.EXTERNAL_APP_FOLDER))

    @Provides
    fun provideRecorderViewModel() = RecorderViewModel(ExternalStorage(AppConfig.EXTERNAL_APP_FOLDER))

}