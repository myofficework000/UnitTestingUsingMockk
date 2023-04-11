package com.example.coroutineretrofitmvvmmockkunittesting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coroutineretrofitmvvmmockkunittesting.model.Repository

class DogViewModelFactory constructor(private val repository: Repository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(DogViewModel::class.java)) {
            DogViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("View model not found")
        }
    }
}