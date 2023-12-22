package com.example.android.politicalpreparedness.representative

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.PoliticalRepository
import com.example.android.politicalpreparedness.repository.Result
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel(private val repository: PoliticalRepository) : ViewModel() {

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

    private val _fieldsValidation = MutableLiveData<String?>()
    val fieldsValidation: LiveData<String?> get() = _fieldsValidation

    private val _representativesState = MutableLiveData<RepresentativesState>()
    val representativesState: LiveData<RepresentativesState> get() = _representativesState

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>> get() = _representatives

    val addressLineOne = MutableLiveData<String>()
    val addressLineTwo = MutableLiveData<String?>()
    val city = MutableLiveData<String>()
    val state = MutableLiveData<String>()
    private val stateCode = MutableLiveData<String>()
    val zip = MutableLiveData<String>()

    fun startGettingRepresentatives() {
        if (validateFields()) {
            _representativesState.value = RepresentativesState.Loading
            viewModelScope.launch {
                val result = repository.getRepresentatives(
                    "${addressLineTwo.value} ${addressLineOne.value}, ${city.value}, ${stateCode.value} ${zip.value}",
                )
                if (result is Result.Success) {
                    _representatives.value = result.data ?: emptyList()
                    _representativesState.value = RepresentativesState.Success
                } else {
                    _representativesState.value = RepresentativesState.Error
                }
            }
        }
    }

    fun setStateCodeByName(stateName: String, context: Context) {
        val states = context.resources.getStringArray(R.array.states)
        val stateCodes = context.resources.getStringArray(R.array.state_codes)

        val index = states.indexOf(stateName)
        stateCode.value = if (index != -1 && index < stateCodes.size) {
            stateCodes[index]
        } else {
            ""
        }
    }

    private fun validateFields(): Boolean {
        return when {
            addressLineOne.value.isNullOrBlank() -> {
                _fieldsValidation.value = "Address Line 1 is missing"
                false
            }

            addressLineTwo.value.isNullOrBlank() -> {
                _fieldsValidation.value = "Address Line 2 is missing"
                false
            }

            city.value.isNullOrBlank() -> {
                _fieldsValidation.value = "City is missing"
                false
            }

            zip.value.isNullOrBlank() -> {
                _fieldsValidation.value = "Zip is missing"
                false
            }

            else -> {
                _fieldsValidation.value = null
                true
            }
        }
    }

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

    sealed class RepresentativesState {
        object Success : RepresentativesState()
        object Error : RepresentativesState()
        object Loading : RepresentativesState()
    }
}
