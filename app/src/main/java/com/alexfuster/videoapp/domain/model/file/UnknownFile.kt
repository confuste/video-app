package com.alexfuster.videoapp.domain.model.file

import java.io.File

class UnknownFile : FileIface {
    override fun mimeType(): String = ""

    override fun absolutePath(): String = ""

    override fun rawFile(): File = File("")
}