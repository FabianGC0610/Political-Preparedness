package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.repository.PoliticalRepository

@Suppress("UNCHECKED_CAST")
class RepresentativeViewModelFactory(
    private val repository: PoliticalRepository,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        run { (RepresentativeViewModel(repository) as T) }
}
