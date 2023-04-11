package com.example.coroutineretrofitmvvmmockkunittesting.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coroutineretrofitmvvmmockkunittesting.model.Dog
import com.example.coroutineretrofitmvvmmockkunittesting.model.Repository
import kotlinx.coroutines.launch

class DogViewModel(private val repository: Repository) : ViewModel() {

    val dogResponse = MutableLiveData<Dog>()
    val error = MutableLiveData<String>()
    val progress = MutableLiveData<Boolean>()

    fun getDog() {
        viewModelScope.launch {
            try {
                progress.postValue(true)
                val response = repository.getRandomDog()
                if (response.isSuccessful) {
                    response.body()?.let {
                        dogResponse.postValue(it)
                        progress.postValue(false)
                    }
                } else {
                    progress.postValue(false)
                    error.postValue(ERROR_MESSAGE)
                }
            } catch (exception: Exception) {
                progress.postValue(false)
                error.postValue(exception.message)
            }
            return@launch
        }
    }

    companion object {
        const val ERROR_MESSAGE = "Internal Server error"
    }
}