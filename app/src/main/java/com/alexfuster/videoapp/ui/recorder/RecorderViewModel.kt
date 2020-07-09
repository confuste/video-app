package com.alexfuster.videoapp.ui.recorder

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexfuster.videoapp.domain.repository.ExternalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecorderViewModel @ViewModelInject constructor(private val repository: ExternalStorage)
    : ViewModel() {


    private val _directoryPath = MutableLiveData<String>()
    val directoryPath: LiveData<String>
        get() = _directoryPath


    fun loadOutputDirectory() {
        viewModelScope.launch {
            _directoryPath.value = withContext(Dispatchers.IO) {
                repository.getOutputDirectory().absolutePath
            }
        }
    }

}