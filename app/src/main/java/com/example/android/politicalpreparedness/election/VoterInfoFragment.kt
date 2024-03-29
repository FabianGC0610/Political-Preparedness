package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.Application
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.utils.showToast

class VoterInfoFragment : Fragment() {

    private lateinit var viewModel: VoterInfoViewModel

    private lateinit var binding: FragmentVoterInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val sharedData = getElectionArgument()
        viewModel = ViewModelProvider(
            this,
            VoterInfoViewModelFactory(
                (requireContext().applicationContext as Application).repository,
                sharedData.first,
                sharedData.second,
            ),
        )[VoterInfoViewModel::class.java]

        binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCanStartGettingVoterInfoObserver()
        setupElectionIsFollowed()
        setupAnErrorOccurredWithDb()
        viewModel.validateReceivedData()
        setupVoterInfoObserver()
        setupVoterInfoStateObserver()
        setupOpenLocationsWebEventObserver()
        setupOpenBallotWebEventObserver()
    }

    private fun getElectionArgument(): Pair<Int, Division> {
        val args = arguments?.let { VoterInfoFragmentArgs.fromBundle(it) }
        return Pair(args?.argElectionId ?: 1, args?.argDivision ?: Division("", "", ""))
    }

    private fun setupVoterInfoStateObserver() {
        viewModel.voterInfoState.observe(viewLifecycleOwner) { voterInfoState ->
            when (voterInfoState) {
                VoterInfoViewModel.VoterInfoState.Success -> {
                    showVoterInfo()
                }

                VoterInfoViewModel.VoterInfoState.Error -> {
                    showVoterInfoError()
                }

                VoterInfoViewModel.VoterInfoState.Loading -> {
                    showVoterInfoLoading()
                }
            }
        }
    }

    private fun setupVoterInfoObserver() {
        viewModel.voterInfo.observe(viewLifecycleOwner) { voterInfo ->
            if (voterInfo != null) {
                viewModel.populateFields()
            }
        }
    }

    private fun setupOpenLocationsWebEventObserver() {
        viewModel.openLocationsWebEvent.observe(viewLifecycleOwner) { openLocationsWeb ->
            if (!openLocationsWeb.isNullOrBlank()) {
                openWebPage(openLocationsWeb)
                viewModel.onOpenLocationsWebEventCompleted()
            }
        }
    }

    private fun setupOpenBallotWebEventObserver() {
        viewModel.openBallotWebEvent.observe(viewLifecycleOwner) { openBallotWeb ->
            if (!openBallotWeb.isNullOrBlank()) {
                openWebPage(openBallotWeb)
                viewModel.onOpenBallotWebEventCompleted()
            }
        }
    }

    private fun setupCanStartGettingVoterInfoObserver() {
        viewModel.canStartGettingVoterInformation.observe(viewLifecycleOwner) { canStartGettingVoterInfo ->
            if (canStartGettingVoterInfo) {
                viewModel.startGettingVoterInfo()
                viewModel.getElectionSavedState()
            } else {
                showInsufficientDataError()
            }
        }
    }

    private fun setupElectionIsFollowed() {
        viewModel.electionIsFollowed.observe(viewLifecycleOwner) { electionIsFollowed ->
            if (electionIsFollowed) {
                binding.button.text = getString(R.string.voter_info_election_followed_button_text)
            } else {
                binding.button.text = getString(R.string.voter_info_election_unfollowed_button_text)
            }
        }
    }

    private fun setupAnErrorOccurredWithDb() {
        viewModel.anErrorOccurredWithDb.observe(viewLifecycleOwner) { anErrorOccurred ->
            if (anErrorOccurred) {
                showToast(getString(R.string.voter_info_db_error), requireContext())
            }
        }
    }

    private fun openWebPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun showVoterInfo() {
        binding.voterInfoProgressBar.visibility = View.GONE
    }

    private fun showVoterInfoLoading() {
        binding.voterInfoProgressBar.visibility = View.VISIBLE
    }

    private fun showVoterInfoError() {
        binding.voterInfoProgressBar.visibility = View.GONE
        binding.voterInfoPlaceholder.visibility = View.VISIBLE
        binding.voterInfoPlaceholder.text = getString(R.string.voter_info_error_getting_data_error)
    }

    private fun showInsufficientDataError() {
        binding.button.visibility = View.GONE
        binding.voterInfoPlaceholder.visibility = View.VISIBLE
        binding.voterInfoPlaceholder.text = getString(R.string.voter_info_insufficient_data_error)
    }
}
