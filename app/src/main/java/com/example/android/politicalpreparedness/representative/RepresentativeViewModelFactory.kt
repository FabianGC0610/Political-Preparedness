package com.example.android.politicalpreparedness.representative

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.android.politicalpreparedness.repository.PoliticalRepository
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class RepresentativeViewModelFactory(
    private val repository: PoliticalRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null,
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle,
    ): T {
        if (modelClass.isAssignableFrom(RepresentativeViewModel::class.java)) {
            return RepresentativeViewModel(repository, handle) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
