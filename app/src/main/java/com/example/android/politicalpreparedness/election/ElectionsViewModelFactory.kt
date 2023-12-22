package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.repository.PoliticalRepository

@Suppress("UNCHECKED_CAST")
class ElectionsViewModelFactory(
    private val repository: PoliticalRepository,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        run { (ElectionsViewModel(repository) as T) }
}
