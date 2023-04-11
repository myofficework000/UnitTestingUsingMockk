package com.example.coroutineretrofitmvvmmockkunittesting.model

class Repository(private val apiService: ApiService) {
    suspend fun getRandomDog() = apiService.getRandomDog()
}