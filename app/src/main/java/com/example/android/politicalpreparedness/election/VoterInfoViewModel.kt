package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.PoliticalRepository
import com.example.android.politicalpreparedness.repository.Result
import com.example.android.politicalpreparedness.source.local.PoliticalLocalDataSource
import kotlinx.coroutines.launch
import java.util.Date

class VoterInfoViewModel(
    private val repository: PoliticalRepository,
    private val electionId: Int?,
    private val division: Division?,
) : ViewModel() {
    private val _voterInfoState = MutableLiveData<VoterInfoState>()
    val voterInfoState: LiveData<VoterInfoState> get() = _voterInfoState

    private val _canStartGettingVoterInformation = MutableLiveData<Boolean>()
    val canStartGettingVoterInformation: LiveData<Boolean> get() = _canStartGettingVoterInformation

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse> get() = _voterInfo

    private val _anErrorOccurredWithDb = MutableLiveData<Boolean>()
    val anErrorOccurredWithDb: LiveData<Boolean> get() = _anErrorOccurredWithDb

    private val _voterInfoName = MutableLiveData<String?>()
    val voterInfoName: LiveData<String?> get() = _voterInfoName

    private val _voterInfoDate = MutableLiveData<String?>()
    val voterInfoDate: LiveData<String?> get() = _voterInfoDate

    private val _canShowStateHeader = MutableLiveData<Boolean>()
    val canShowStateHeader: LiveData<Boolean> get() = _canShowStateHeader

    private val _electionIsFollowed = MutableLiveData<Boolean>()
    val electionIsFollowed: LiveData<Boolean> get() = _electionIsFollowed

    private val _voterInfoLocationsLink = MutableLiveData<String?>()
    val voterInfoLocationsLink: LiveData<String?> get() = _voterInfoLocationsLink

    private val _voterInfoBallotLink = MutableLiveData<String?>()
    val voterInfoBallotLink: LiveData<String?> get() = _voterInfoBallotLink

    private val _voterInfoAddress = MutableLiveData<String?>()
    val voterInfoAddress: LiveData<String?> get() = _voterInfoAddress

    private val _openLocationsWebEvent = MutableLiveData<String?>()
    val openLocationsWebEvent: LiveData<String?> get() = _openLocationsWebEvent

    private val _openBallotWebEvent = MutableLiveData<String?>()
    val openBallotWebEvent: LiveData<String?> get() = _openBallotWebEvent

    fun validateReceivedData() {
        _canStartGettingVoterInformation.value =
            !((electionId == null || electionId == 1) || (division?.state.isNullOrBlank()) || division?.country.isNullOrBlank())
    }

    fun startGettingVoterInfo() {
        _voterInfoState.value = VoterInfoState.Loading
        viewModelScope.launch {
            val result = repository.getVoterInfo(
                "${division?.state}, ${division?.country}",
                electionId ?: 1,
            )
            if (result is Result.Success) {
                _voterInfo.value = result.data ?: VoterInfoResponse(
                    Election(
                        1,
                        "",
                        Date(),
                        Division("", "", ""),
                    ),
                )
                _voterInfoState.value = VoterInfoState.Success
            } else {
                _voterInfoState.value = VoterInfoState.Error
            }
        }
    }

    fun getElectionSavedState() {
        viewModelScope.launch {
            val result = repository.getElection(electionId ?: _voterInfo.value?.election?.id ?: 1)
            if (result is Result.Success) {
                _electionIsFollowed.value = true
                _anErrorOccurredWithDb.value = false
            } else if (result is Result.Error) {
                if (result.message == PoliticalLocalDataSource.ELECTION_NOT_FOUND) {
                    _electionIsFollowed.value = false
                    _anErrorOccurredWithDb.value = false
                } else {
                    _anErrorOccurredWithDb.value = true
                }
            }
        }
    }

    fun onButtonClickedEvent() {
        if (_electionIsFollowed.value == true) {
            unfollowElection()
        } else {
            followElection()
        }
    }

    private fun followElection() {
        viewModelScope.launch {
            repository.addElectionToSaved(_voterInfo.value?.election!!)
            getElectionSavedState()
        }
    }

    private fun unfollowElection() {
        viewModelScope.launch {
            repository.deleteElection(_voterInfo.value?.election?.id!!)
            getElectionSavedState()
        }
    }

    fun populateFields() {
        _voterInfoName.value = _voterInfo.value?.election?.name
        _voterInfoDate.value = _voterInfo.value?.election?.electionDay.toString()
        _voterInfoLocationsLink.value =
            _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
        _voterInfoBallotLink.value =
            _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
        setCanShowStateHeader()
        val addressLineOne =
            _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.correspondenceAddress?.line1 ?: ""
        val city =
            _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.correspondenceAddress?.city ?: ""
        val state =
            _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.correspondenceAddress?.state ?: ""
        val zip =
            _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.correspondenceAddress?.zip ?: ""
        _voterInfoAddress.value = "$addressLineOne $zip, $city, $state"
    }

    private fun setCanShowStateHeader() {
        _canShowStateHeader.value =
            !(_voterInfoLocationsLink.value == null && _voterInfoBallotLink.value == null)
    }

    fun onOpenLocationsWebEvent() {
        _openLocationsWebEvent.value = _voterInfoLocationsLink.value
    }

    fun onOpenLocationsWebEventCompleted() {
        _openLocationsWebEvent.value = null
    }

    fun onOpenBallotWebEvent() {
        _openBallotWebEvent.value = _voterInfoBallotLink.value
    }

    fun onOpenBallotWebEventCompleted() {
        _openBallotWebEvent.value = null
    }

    sealed class VoterInfoState {
        object Success : VoterInfoState()
        object Error : VoterInfoState()
        object Loading : VoterInfoState()
    }

    // TODO: Add var and methods to save and remove elections to local database
    // TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */
}
