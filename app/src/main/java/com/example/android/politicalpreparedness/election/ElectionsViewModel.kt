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

    private val _electionClickedEvent = MutableLiveData<Election?>()
    val electionClickedEvent: LiveData<Election?> get() = _electionClickedEvent

    private val _navigateBackEvent = MutableLiveData<Boolean>()
    val navigateBackEvent: LiveData<Boolean> get() = _navigateBackEvent

    private val _upcomingElectionsState = MutableLiveData<UpcomingElectionsState>()
    val upcomingElectionsState: LiveData<UpcomingElectionsState> get() = _upcomingElectionsState

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>> get() = _upcomingElections

    private val _savedElectionsState = MutableLiveData<SavedElectionsState>()
    val savedElectionsState: LiveData<SavedElectionsState> get() = _savedElectionsState

    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>> get() = _savedElections

    fun startGettingUpcomingElections() {
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

    fun startGettingSavedElections() {
        _savedElectionsState.value = SavedElectionsState.Loading
        viewModelScope.launch {
            val result = repository.getAllElections()
            if (result is Result.Success) {
                _savedElectionsState.value = SavedElectionsState.Success
                if (result.data.isEmpty()) {
                    _savedElectionsState.value = SavedElectionsState.Empty
                } else {
                    _savedElections.value = result.data ?: emptyList()
                    _savedElectionsState.value = SavedElectionsState.Success
                }
                _savedElections.value = result.data ?: emptyList()
                _savedElectionsState.value = SavedElectionsState.Success
            } else {
                _savedElectionsState.value = SavedElectionsState.Error
            }
        }
    }

    fun onElectionClickedEvent(election: Election) {
        _electionClickedEvent.value = election
    }

    fun onElectionClickedEventCompleted() {
        _electionClickedEvent.value = null
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

    sealed class SavedElectionsState {
        object Success : SavedElectionsState()
        object Empty : SavedElectionsState()
        object Error : SavedElectionsState()
        object Loading : SavedElectionsState()
    }

    // TODO: Create live data val for saved elections

    // TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    // TODO: Create functions to navigate to saved or upcoming election voter info
}
