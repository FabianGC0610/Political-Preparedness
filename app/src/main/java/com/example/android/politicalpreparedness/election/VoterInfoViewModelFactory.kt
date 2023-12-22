package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.repository.PoliticalRepository

@Suppress("UNCHECKED_CAST")
class VoterInfoViewModelFactory(
    private val repository: PoliticalRepository,
    private val electionId: Int,
    private val division: Division,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        run { (VoterInfoViewModel(repository, electionId, division) as T) }
}
