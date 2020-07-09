package com.alexfuster.videoapp.domain.model.file

import com.alexfuster.videoapp.app.constants.FileConfig
import java.io.File

class VideoFile(private val file: File): FileIface {


    override fun mimeType(): String = FileConfig.VIDEO_MIME_TYPE

    override fun absolutePath(): String = file.absolutePath

    override fun rawFile(): File = file


}