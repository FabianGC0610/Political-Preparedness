package com.example.android.politicalpreparedness.launch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding

class LaunchFragment : Fragment() {

    private val viewModel: LaunchViewModel by lazy {
        ViewModelProvider(this)[(LaunchViewModel::class.java)]
    }

    private lateinit var binding: FragmentLaunchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLaunchBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.upcomingElectionsEvent.observe(viewLifecycleOwner) { upcomingElectionsButtonClicked ->
            if (upcomingElectionsButtonClicked) {
                navToElections()
                viewModel.onUpcomingElectionsEventCompleted()
            }
        }

        viewModel.findRepresentativesEvent.observe(viewLifecycleOwner) { findRepresentativesButtonClicked ->
            if (findRepresentativesButtonClicked) {
                navToRepresentatives()
                viewModel.onFindRepresentativesEventCompleted()
            }
        }

        return binding.root
    }

    private fun navToElections() {
        this.findNavController()
            .navigate(LaunchFragmentDirections.actionLaunchFragmentToElectionsFragment())
    }

    private fun navToRepresentatives() {
        this.findNavController()
            .navigate(LaunchFragmentDirections.actionLaunchFragmentToRepresentativeFragment())
    }
}
