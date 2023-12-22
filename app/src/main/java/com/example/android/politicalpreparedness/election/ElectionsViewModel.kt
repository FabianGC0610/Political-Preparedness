package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.PoliticalRepository
import com.example.android.politicalpreparedness.repository.Result
import kotlinx.coroutines.launch

class ElectionsViewModel(private val repository: PoliticalRepository) : ViewModel() {

    private val _upcomingElectionClickedEvent = MutableLiveData<Election?>()
    val upcomingElectionClickedEvent: LiveData<Election?> get() = _upcomingElectionClickedEvent

    private val _navigateBackEvent = MutableLiveData<Boolean>()
    val navigateBackEvent: LiveData<Boolean> get() = _navigateBackEvent

    private val _upcomingElectionsState = MutableLiveData<UpcomingElectionsState>()
    val upcomingElectionsState: LiveData<UpcomingElectionsState> get() = _upcomingElectionsState

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>> get() = _upcomingElections

    init {
        startGettingUpcomingElections()
    }

    private fun startGettingUpcomingElections() {
        _upcomingElectionsState.value = UpcomingElectionsState.Loading
        viewModelScope.launch {
            val result = repository.getUpcomingElections()
            if (result is Result.Success) {
                _upcomingElections.value = result.data ?: emptyList()
                _upcomingElectionsState.value = UpcomingElectionsState.Success
            } else {
                _upcomingElectionsState.value = UpcomingElectionsState.Error
            }
        }
    }

    fun onUpcomingElectionClickedEvent(election: Election) {
        _upcomingElectionClickedEvent.value = election
    }

    fun onUpcomingElectionClickedEventCompleted() {
        _upcomingElectionClickedEvent.value = null
    }

    fun onNavigateBackEvent() {
        _navigateBackEvent.value = true
    }

    fun onNavigateBackEventCompleted() {
        _navigateBackEvent.value = false
    }

    sealed class UpcomingElectionsState {
        object Success : UpcomingElectionsState()
        object Error : UpcomingElectionsState()
        object Loading : UpcomingElectionsState()
    }

    // TODO: Create live data val for saved elections

    // TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    // TODO: Create functions to navigate to saved or upcoming election voter info
}
