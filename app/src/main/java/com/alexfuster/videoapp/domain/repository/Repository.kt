package com.alexfuster.videoapp.domain.repository

import com.alexfuster.videoapp.domain.model.file.FileIface

interface Repository {

    fun getFiles(): List<FileIface>

    fun getFilesByMimeType (mimeType: String): List<FileIface>

    fun removeFile(path: String): Boolean

}