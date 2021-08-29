package com.example.weatherappmvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherappmvvm.repository.Repository

class ViewModelFactory(private var repository: Repository, private val source: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when (source) {
            "register" -> return RegisterViewModel(repository) as T
            "main" -> return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}