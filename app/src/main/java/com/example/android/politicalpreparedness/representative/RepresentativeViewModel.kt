package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative

class RepresentativeViewModel : ViewModel() {

    private val _representativeClickedEvent = MutableLiveData<Representative?>()
    val representativeClickedEvent: LiveData<Representative?> get() = _representativeClickedEvent

    private val _findRepresentativeEvent = MutableLiveData<Boolean>()
    val findRepresentativeEvent: LiveData<Boolean> get() = _findRepresentativeEvent

    private val _useLocationEvent = MutableLiveData<Boolean>()
    val useLocationEvent: LiveData<Boolean> get() = _useLocationEvent

    private val _locationPermissionGranted = MutableLiveData<Boolean>()
    val locationPermissionGranted: LiveData<Boolean> get() = _locationPermissionGranted

    private val _locationPermissionActivated = MutableLiveData<Boolean>()
    val locationPermissionActivated: LiveData<Boolean> get() = _locationPermissionActivated

    private val _navigateBackEvent = MutableLiveData<Boolean>()
    val navigateBackEvent: LiveData<Boolean> get() = _navigateBackEvent

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address> get() = _address

    val addressLineOne = MutableLiveData<String>()
    val addressLineTwo = MutableLiveData<String?>()
    val city = MutableLiveData<String>()
    val state = MutableLiveData<String>()
    val zip = MutableLiveData<String>()

    fun setLocationPermissionGranted() {
        _locationPermissionGranted.value = true
    }

    fun setLocationPermissionActivated() {
        _locationPermissionActivated.value = true
    }

    fun saveAddress(address: Address) {
        _address.value = address
    }

    fun setAddressValues() {
        addressLineOne.value = _address.value?.line1
        addressLineTwo.value = _address.value?.line2
        city.value = _address.value?.city
        state.value = _address.value?.state
        zip.value = _address.value?.zip
    }

    fun setInitialState(states: Array<String>) {
        if (state.value != null) {
            state.value = states[0]
        }
    }

    fun onNavigateBackEvent() {
        _navigateBackEvent.value = true
    }

    fun onNavigateBackEventCompleted() {
        _navigateBackEvent.value = false
    }

    fun onRepresentativeClickedEvent(representative: Representative) {
        _representativeClickedEvent.value = representative
    }

    fun onRepresentativeClickedEventCompleted() {
        _representativeClickedEvent.value = null
    }

    fun onFindRepresentativeEvent() {
        _findRepresentativeEvent.value = true
    }

    fun onFindRepresentativeEventCompleted() {
        _findRepresentativeEvent.value = false
    }

    fun onUseLocationEvent() {
        _useLocationEvent.value = true
    }

    fun onUseLocationEventCompleted() {
        _useLocationEvent.value = false
    }

    // TODO: Establish live data for representatives and address

    // TODO: Create function to fetch representatives from API from a provided address

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

     val (offices, officials) = getRepresentativesDeferred.await()
     _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

     Note: getRepresentatives in the above code represents the method used to fetch data from the API
     Note: _representatives in the above code represents the established mutable live data housing representatives

     */
}
