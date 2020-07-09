package com.alexfuster.videoapp.domain.model.file

import java.io.File

interface FileIface {

    fun mimeType(): String

    fun absolutePath(): String

    fun rawFile(): File
}