package com.alexfuster.videoapp.domain.repository

import android.os.Environment
import com.alexfuster.videoapp.app.exception.TypeFileNotFound
import com.alexfuster.videoapp.domain.factory.FileFactory
import com.alexfuster.videoapp.domain.model.file.FileIface
import com.alexfuster.videoapp.domain.model.file.UnknownFile
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton



class ExternalStorage constructor(private val appFolder: String) : Repository {


    override fun getFiles(): List<FileIface> {
        val directoryFile = getOutputDirectory()
        val fileList: Array<File>? = directoryFile.listFiles()

        val repositoryFileList =  fileList?.map {
            try { FileFactory.createFile(it) }
            catch (e: TypeFileNotFound) { UnknownFile() }
        }

        val onlyKnownFiles = repositoryFileList?.filter { it !is UnknownFile }

        return if(onlyKnownFiles.isNullOrEmpty()) emptyList() else onlyKnownFiles
    }

    override fun getFilesByMimeType(mimeType: String): List<FileIface> =
        getFiles().filter { it.mimeType() == mimeType }


    override fun removeFile(path: String): Boolean = File(path).delete()


    fun getOutputDirectory(): File {
        return File(Environment.getExternalStorageDirectory(),
            appFolder).apply { this.mkdirs() }


    }

}