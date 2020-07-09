package com.alexfuster.videoapp.ui.gallery

import android.content.Context
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexfuster.videoapp.domain.model.file.FileIface
import com.alexfuster.videoapp.domain.repository.ExternalStorage
import com.alexfuster.videoapp.ui.gallery.recyclerview.ItemModel
import com.alexfuster.videoapp.ui.utils.getUriFromFile

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GalleryViewModel @ViewModelInject constructor(private val repository: ExternalStorage) : ViewModel() {


    data class DeleteFile(val isDelete: Boolean, val position: Int)

    private val _fileList = MutableLiveData<List<ItemModel>>()
    val fileList: LiveData<List<ItemModel>>
        get() = _fileList

    private val _fileDeleted = MutableLiveData<DeleteFile>()
    val fileDeleted: LiveData<DeleteFile>
        get() = _fileDeleted



    fun loadFiles(context: Context, mimeType: String) {
        viewModelScope.launch {
            _fileList.value = withContext(Dispatchers.IO) {
                val fileList: List<FileIface> = repository.getFilesByMimeType(mimeType)
                fileList.map { ItemModel(it.absolutePath(),  it.getUriFromFile(context) ) }
            }
        }
    }

    fun removeFile(filePath: String, position: Int) {
        viewModelScope.launch {
            _fileDeleted.value = withContext(Dispatchers.IO) {
                DeleteFile(repository.removeFile(filePath), position)
            }
        }
    }




}