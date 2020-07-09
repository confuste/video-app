package com.alexfuster.videoapp.ui.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.alexfuster.videoapp.domain.model.file.FileIface

fun FileIface.getUriFromFile(context: Context) : Uri {

    val videoURI = FileProvider.getUriForFile(context,
        context.applicationContext.packageName.toString() + ".provider", this.rawFile())
    return videoURI
}