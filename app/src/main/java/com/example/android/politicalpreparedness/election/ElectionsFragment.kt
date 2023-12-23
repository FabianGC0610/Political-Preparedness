package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.Application
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.models.Election

class ElectionsFragment : Fragment() {

    private lateinit var viewModel: ElectionsViewModel

    private lateinit var upcomingElectionsAdapter: ElectionListAdapter

    private lateinit var savedElectionsAdapter: ElectionListAdapter

    private lateinit var binding: FragmentElectionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(
            this,
            ElectionsViewModelFactory(
                (requireContext().applicationContext as Application).repository,
            ),
        )[(ElectionsViewModel::class.java)]
        binding = FragmentElectionBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupUpcomingElectionsListAdapter()
        setupSavedElectionsListAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUpcomingElectionsStateObserver()
        setupSavedElectionsStateObserver()
        setupNavigationBackEventObserver()
        setupUpcomingElectionClickedObserver()
        viewModel.startGettingUpcomingElections()
        viewModel.startGettingSavedElections()
    }

    private fun setupUpcomingElectionsListAdapter() {
        upcomingElectionsAdapter = ElectionListAdapter(
            ElectionListener { election ->
                viewModel.onElectionClickedEvent(election)
                binding.upcomingElectionsList.contentDescription = election.name
            },
        )
        binding.upcomingElectionsList.adapter = upcomingElectionsAdapter
    }

    private fun setupSavedElectionsListAdapter() {
        savedElectionsAdapter = ElectionListAdapter(
            ElectionListener { election ->
                viewModel.onElectionClickedEvent(election)
                binding.upcomingElectionsList.contentDescription = election.name
            },
        )
        binding.savedElectionsList.adapter = savedElectionsAdapter
    }

    private fun setupUpcomingElectionClickedObserver() {
        viewModel.electionClickedEvent.observe(viewLifecycleOwner) { electionClicked ->
            if (electionClicked != null) {
                navToVoterInfo(electionClicked)
                viewModel.onElectionClickedEventCompleted()
            }
        }
    }

    private fun setupNavigationBackEventObserver() {
        viewModel.navigateBackEvent.observe(viewLifecycleOwner) { navigateBack ->
            if (navigateBack) {
                findNavController().popBackStack()
                viewModel.onNavigateBackEventCompleted()
            }
        }
    }

    private fun setupUpcomingElectionsStateObserver() {
        viewModel.upcomingElectionsState.observe(viewLifecycleOwner) { upcomingElectionsState ->
            when (upcomingElectionsState) {
                ElectionsViewModel.UpcomingElectionsState.Success -> {
                    showUpcomingElectionsList()
                }

                ElectionsViewModel.UpcomingElectionsState.Error -> {
                    showUpcomingElectionsError()
                }

                ElectionsViewModel.UpcomingElectionsState.Loading -> {
                    showUpcomingElectionsLoading()
                }
            }
        }
    }

    private fun setupSavedElectionsStateObserver() {
        viewModel.savedElectionsState.observe(viewLifecycleOwner) { savedElectionsState ->
            when (savedElectionsState) {
                ElectionsViewModel.SavedElectionsState.Success -> {
                    showSavedElectionsList()
                }

                ElectionsViewModel.SavedElectionsState.Error -> {
                    showSavedElectionsError()
                }

                ElectionsViewModel.SavedElectionsState.Empty -> {
                    showSavedElectionsEmptyError()
                }

                ElectionsViewModel.SavedElectionsState.Loading -> {
                    showSavedElectionsLoading()
                }
            }
        }
    }

    private fun showUpcomingElectionsList() {
        binding.upcomingElectionsList.visibility = View.VISIBLE
        binding.upcomingElectionsProgressBar.visibility = View.GONE
    }

    private fun showUpcomingElectionsLoading() {
        binding.upcomingElectionsList.visibility = View.GONE
        binding.upcomingElectionsProgressBar.visibility = View.VISIBLE
    }

    private fun showUpcomingElectionsError() {
        binding.upcomingElectionsList.visibility = View.GONE
        binding.upcomingElectionsProgressBar.visibility = View.GONE
        binding.upcomingElectionsPlaceholder.visibility = View.VISIBLE
        binding.upcomingElectionsPlaceholder.text =
            getString(R.string.elections_upcoming_elections_retrieving_error)
    }

    private fun showSavedElectionsList() {
        binding.savedElectionsList.visibility = View.VISIBLE
        binding.savedElectionsProgressBar.visibility = View.GONE
    }

    private fun showSavedElectionsLoading() {
        binding.savedElectionsList.visibility = View.GONE
        binding.savedElectionsProgressBar.visibility = View.VISIBLE
    }

    private fun showSavedElectionsError() {
        binding.savedElectionsList.visibility = View.GONE
        binding.savedElectionsProgressBar.visibility = View.GONE
        binding.savedElectionsPlaceholder.visibility = View.VISIBLE
        binding.savedElectionsPlaceholder.text =
            getString(R.string.elections_saved_elections_retrieving_error)
    }

    private fun showSavedElectionsEmptyError() {
        binding.savedElectionsList.visibility = View.GONE
        binding.savedElectionsProgressBar.visibility = View.GONE
        binding.savedElectionsPlaceholder.visibility = View.VISIBLE
        binding.savedElectionsPlaceholder.text =
            getString(R.string.elections_saved_elections_empty_error)
    }

    private fun navToVoterInfo(election: Election) {
        this.findNavController().navigate(
            ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                election.id,
                election.division,
            ),
        )
    }
}
