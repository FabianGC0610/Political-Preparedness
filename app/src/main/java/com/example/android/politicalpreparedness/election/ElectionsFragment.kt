package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.Application
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.models.Election

class ElectionsFragment : Fragment() {

    private val viewModel: ElectionsViewModel by lazy {
        ViewModelProvider(
            this,
            ElectionsViewModelFactory(
                (requireContext().applicationContext as Application).repository,
            ),
        )[(ElectionsViewModel::class.java)]
    }

    private lateinit var upcomingElectionsAdapter: ElectionListAdapter

    private lateinit var binding: FragmentElectionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentElectionBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupUpcomingElectionsListAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUpcomingElectionsStateObserver()
        setupNavigationBackEventObserver()
        setupUpcomingElectionClickedObserver()
    }

    private fun setupUpcomingElectionsListAdapter() {
        upcomingElectionsAdapter = ElectionListAdapter(
            ElectionListener { election ->
                viewModel.onUpcomingElectionClickedEvent(election)
                binding.upcomingElectionsList.contentDescription = election.name
            },
        )
        binding.upcomingElectionsList.adapter = upcomingElectionsAdapter
    }

    private fun setupUpcomingElectionClickedObserver() {
        viewModel.upcomingElectionClickedEvent.observe(viewLifecycleOwner) { upcomingElectionClicked ->
            if (upcomingElectionClicked != null) {
                navToVoterInfo(upcomingElectionClicked)
                viewModel.onUpcomingElectionClickedEventCompleted()
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
        // TODO: Add error visual
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
