package com.example.coroutineretrofitmvvmmockkunittesting.model

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("random")
    suspend fun getRandomDog(): Response<Dog>

    companion object {
        fun getInstance() = ApiClient.retrofit.create(ApiService::class.java)
    }
}