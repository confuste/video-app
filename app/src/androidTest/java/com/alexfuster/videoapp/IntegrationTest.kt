package com.alexfuster.videoapp

import com.alexfuster.videoapp.domain.repository.ExternalStorage
import org.junit.*
import java.io.File
import java.nio.file.Files

class IntegrationTest {

    private val folderName = "Test_Folder"

    @Before
    fun cleanFolderAndContentBefore() {
        val deleteRepository = ExternalStorage(folderName)
        deleteRepository.getOutputDirectory().deleteRecursively()
    }

    @After
    fun cleanFolderAndContentAfter() {
        val deleteRepository = ExternalStorage(folderName)
        deleteRepository.getOutputDirectory().deleteRecursively()
    }

    @Test
    fun whenGetOutputDirectory_shouldCreateNamedFolder() {
        val repository = ExternalStorage(folderName)
        val rootDirectory: File = repository.getOutputDirectory()
        Assert.assertEquals(rootDirectory.name, folderName)
    }


    @Test
    fun whenGetOutputDirectory_shouldCreateEmptyFolderOnDevice() {
        val repository = ExternalStorage(folderName)
        Assert.assertTrue("Folder must be Empty", repository.getFiles().isEmpty())
    }



    @Test
    fun whenGetOutputDirectory_shouldGetOnlyVideoMp4Files() {
        val repository = ExternalStorage(folderName)

        val file1 = File(repository.getOutputDirectory(), "video1.mp4")
        Files.createFile(file1.toPath())

        val file2 = File(repository.getOutputDirectory(), "video2.mp4")
        Files.createFile(file2.toPath())

        val file3 = File(repository.getOutputDirectory(), "txtFile.txt")
        Files.createFile(file3.toPath())

        Assert.assertEquals(2, repository.getFiles().size)
    }


}