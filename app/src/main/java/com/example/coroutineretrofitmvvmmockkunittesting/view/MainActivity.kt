package com.example.coroutineretrofitmvvmmockkunittesting.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.coroutineretrofitmvvmmockkunittesting.databinding.ActivityMainBinding
import com.example.coroutineretrofitmvvmmockkunittesting.model.ApiService
import com.example.coroutineretrofitmvvmmockkunittesting.model.Repository
import com.example.coroutineretrofitmvvmmockkunittesting.viewmodel.DogViewModel
import com.example.coroutineretrofitmvvmmockkunittesting.viewmodel.DogViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dogViewModel: DogViewModel
    private lateinit var dogViewModelFactory: DogViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()
        setUpViews()
        setUpObserver()
    }

    private fun initViewModel() {
        dogViewModelFactory = DogViewModelFactory(Repository(ApiService.getInstance()))
        dogViewModel = ViewModelProvider(this, dogViewModelFactory)[DogViewModel::class.java]
    }

    private fun setUpObserver() {
        dogViewModel.dogResponse.observe(this) {
            Glide.with(this)
                .load(it.message)
                .into(binding.imageOfDog)
        }

        dogViewModel.progress.observe(this) {
            if (it) {
                binding.loadingSpinner.visibility = View.VISIBLE
            } else {
                binding.loadingSpinner.visibility = View.GONE
            }
        }

        dogViewModel.error.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpViews() {
        binding.btnSearch.setOnClickListener {
            dogViewModel.getDog()
        }
    }
}