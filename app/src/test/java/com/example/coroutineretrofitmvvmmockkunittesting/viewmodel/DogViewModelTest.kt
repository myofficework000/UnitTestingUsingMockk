package com.example.coroutineretrofitmvvmmockkunittesting.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.coroutineretrofitmvvmmockkunittesting.model.Constants
import com.example.coroutineretrofitmvvmmockkunittesting.model.Dog
import com.example.coroutineretrofitmvvmmockkunittesting.model.Repository
import com.example.coroutineretrofitmvvmmockkunittesting.utils.MainDispatcherRule
import com.google.gson.Gson
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class DogViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<Repository>()
    private val successObserver = mockk<Observer<Dog>>(relaxUnitFun = true)
    private val failureObserver = mockk<Observer<String>>(relaxed = true)
    private val progressObserver = mockk<Observer<Boolean>>(relaxed = true)
    private lateinit var viewModel: DogViewModel

    @Before
    fun setUp() {
        viewModel = DogViewModel(repository)
    }

    @Test
    fun `GIVEN success Stub or Mock data WHEN getRandomDog invoked THEN received success result`() {
        runTest {
            val responseFromAPI = Response.success(
                Gson().fromJson(
                    Constants.SUCCESS_RESULT_WITH_DATA,
                    Dog::class.java
                )
            )

            coEvery { repository.getRandomDog() } returns responseFromAPI

            viewModel.apply {
                progress.observeForever(progressObserver)
                dogResponse.observeForever(successObserver)

                getDog()

                coVerify {
                    progressObserver.onChanged(true)
                    repository.getRandomDog()
                    progressObserver.onChanged(false)
                }

                val expectedResult =
                    Gson().fromJson(Constants.SUCCESS_RESULT_WITH_DATA, Dog::class.java)

                coVerify { successObserver.onChanged(expectedResult) }
            }
        }
    }

    @Test
    fun `GIVEN success Stub or Mock data WHEN getRandomDog invoked THEN received success with no data result`() {
        runTest {
            val responseFromAPI = Response.success(
                Gson().fromJson(
                    Constants.SUCCESS_RESULT_WITHOUT_DATA,
                    Dog::class.java
                )
            )

            coEvery { repository.getRandomDog() } returns responseFromAPI
            viewModel.apply {
                progress.observeForever(progressObserver)
                dogResponse.observeForever(successObserver)

                getDog()

                coVerify {
                    progressObserver.onChanged(true)
                    repository.getRandomDog()
                    progressObserver.onChanged(false)
                }

                val expectedResult =
                    Gson().fromJson(Constants.SUCCESS_RESULT_WITHOUT_DATA, Dog::class.java)

                coVerify { successObserver.onChanged(expectedResult) }
            }
        }
    }

    @Test
    fun `GIVEN failure Stub or Mock data WHEN getRandomDog invoked THEN received failure result`() {
        runTest {
            Response.error<String>(
                500,
                TEST_ERROR_MESSAGE.toResponseBody("text/plain".toMediaType())
            )

            coEvery { repository.getRandomDog().isSuccessful } returns false

            viewModel.apply {
                progress.observeForever(progressObserver)
                error.observeForever(failureObserver)

                getDog()

                coVerify {
                    progressObserver.onChanged(true)
                    repository.getRandomDog()
                    progressObserver.onChanged(false)
                    failureObserver.onChanged(TEST_ERROR_MESSAGE)
                }
            }
        }
    }

    @Test(expected = AssertionError::class)
    fun `GIVEN  no internet WHEN getRandomDog invoked THEN received exception as result`() {
        runTest {
            coEvery { repository.getRandomDog() } throws AssertionError(TEST_EXCEPTION)

            viewModel.apply {
                progress.observeForever(progressObserver)
                error.observeForever(failureObserver)

                getDog()

                coVerify {
                    progressObserver.onChanged(true)
                    repository.getRandomDog()
                    progressObserver.onChanged(false)
                    failureObserver.onChanged(TEST_EXCEPTION)
                }
            }
        }
    }

    @After
    fun tearDown() {
        viewModel.apply {
            if (dogResponse.hasActiveObservers()) {
                dogResponse.removeObserver(successObserver)
            }
            if (error.hasActiveObservers()) {
                error.removeObserver(failureObserver)
            }
            progress.removeObserver(progressObserver)
        }
    }

    private companion object {
        const val TEST_ERROR_MESSAGE = "Internal Server error"
        const val TEST_EXCEPTION = "NO INTERNET AVAILABLE"
    }
}
