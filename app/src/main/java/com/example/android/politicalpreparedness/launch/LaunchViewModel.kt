package com.example.android.politicalpreparedness.launch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LaunchViewModel : ViewModel() {
    private val _upcomingElectionsEvent = MutableLiveData<Boolean>()
    val upcomingElectionsEvent: LiveData<Boolean> get() = _upcomingElectionsEvent

    private val _findRepresentativesEvent = MutableLiveData<Boolean>()
    val findRepresentativesEvent: LiveData<Boolean> get() = _findRepresentativesEvent

    fun onUpcomingElectionsEvent() {
        _upcomingElectionsEvent.value = true
    }

    fun onUpcomingElectionsEventCompleted() {
        _upcomingElectionsEvent.value = false
    }

    fun onFindRepresentativesEvent() {
        _findRepresentativesEvent.value = true
    }

    fun onFindRepresentativesEventCompleted() {
        _findRepresentativesEvent.value = false
    }
}
