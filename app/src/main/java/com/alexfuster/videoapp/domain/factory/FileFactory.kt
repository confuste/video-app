package com.alexfuster.videoapp.domain.factory

import android.webkit.MimeTypeMap
import com.alexfuster.videoapp.app.constants.FileConfig
import com.alexfuster.videoapp.app.exception.TypeFileNotFound
import com.alexfuster.videoapp.domain.model.file.FileIface
import com.alexfuster.videoapp.domain.model.file.VideoFile
import java.io.File

object FileFactory {
    fun createFile(file: File)  : FileIface {

        when (val mimeType = getMimeType(file.absolutePath)) {
            FileConfig.VIDEO_MIME_TYPE -> return VideoFile(file)
            else -> throw TypeFileNotFound("Type $mimeType not found")
        }
    }
}

private fun getMimeType(path: String): String? {
    var type: String? = null
    val extension = MimeTypeMap.getFileExtensionFromUrl(path)
    if (extension != null) {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    return type
}
