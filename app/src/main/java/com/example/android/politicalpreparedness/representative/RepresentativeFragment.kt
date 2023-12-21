package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListener
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.lang.Exception
import java.util.Locale

private const val TAG = "RepresentativeFragment"

class RepresentativeFragment : Fragment() {

    private val viewModel: RepresentativeViewModel by lazy {
        ViewModelProvider(this)[(RepresentativeViewModel::class.java)]
    }

    private lateinit var binding: FragmentRepresentativeBinding

    private lateinit var statesArray: Array<String>

    private val requestLocationPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.setLocationPermissionGranted()
            }
        }

    private val turnOnLocationActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.setLocationPermissionActivated()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRepresentativeBinding.inflate(inflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupRepresentativeListAdapter()

        // TODO: Populate Representative adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRepresentativeClickedEventObserver()
        setupPermissionGrantedObserver()
        setupFindRepresentativeEventObserver()
        setupUseLocationEventObserver()
        setupPermissionActivatedObserver()
        setupNavigationBackEventObserver()
        setupAddressObserver()
        setupStateSpinnerAdapter()
        setInitialState()
    }

    private fun setupPermissionGrantedObserver() {
        viewModel.locationPermissionGranted.observe(viewLifecycleOwner) { permissionGranted ->
            if (permissionGranted) {
                checkDeviceLocationSettings()
            }
        }
    }

    private fun setupPermissionActivatedObserver() {
        viewModel.locationPermissionActivated.observe(viewLifecycleOwner) { permissionActivated ->
            if (permissionActivated) {
                getLocation()
                hideKeyboard()
            }
        }
    }

    private fun setupAddressObserver() {
        viewModel.address.observe(viewLifecycleOwner) { address ->
            if (address != null) {
                viewModel.setAddressValues()
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

    private fun setupRepresentativeClickedEventObserver() {
        viewModel.representativeClickedEvent.observe(viewLifecycleOwner) { representative ->
            representative?.let {
                // TODO: Add new screen and navigation action for representative details
                viewModel.onRepresentativeClickedEventCompleted()
            }
        }
    }

    private fun setupFindRepresentativeEventObserver() {
        viewModel.findRepresentativeEvent.observe(viewLifecycleOwner) { findRepresentative ->
            if (findRepresentative) {
                hideKeyboard()
                viewModel.onFindRepresentativeEventCompleted()
            }
        }
    }

    private fun setupUseLocationEventObserver() {
        viewModel.useLocationEvent.observe(viewLifecycleOwner) { useLocation ->
            if (useLocation) {
                checkLocationPermissions()
                viewModel.onUseLocationEventCompleted()
            }
        }
    }

    private fun setInitialState() {
        viewModel.setInitialState(statesArray)
    }

    private fun setupStateSpinnerAdapter() {
        statesArray = requireContext().resources.getStringArray(R.array.states)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statesArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.state.adapter = adapter
    }

    private fun setupRepresentativeListAdapter() {
        val adapter = RepresentativeListAdapter(
            RepresentativeListener { representative ->
                viewModel.onRepresentativeClickedEvent(representative)
                binding.representativesList.contentDescription =
                    representative.official.name
            },
        )
        binding.representativesList.adapter = adapter
    }

    private fun checkLocationPermissions() {
        if (isPermissionGranted()) {
            viewModel.setLocationPermissionGranted()
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun isPermissionGranted(): Boolean {
        val permissionCheck = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
        return permissionCheck == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val fusedLocationsClient =
            context?.let { LocationServices.getFusedLocationProviderClient(it) }
        fusedLocationsClient?.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                location?.let {
                    val address = geoCodeLocation(it)
                    viewModel.saveAddress(address)
                }
            }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = context?.let { Geocoder(it, Locale.getDefault()) }
        val addresses: MutableList<android.location.Address>? =
            geocoder?.getFromLocation(location.latitude, location.longitude, 1)
        if (geocoder != null) {
            try {
                if (addresses?.isNotEmpty() == true) {
                    val address = addresses[0]
                    return Address(
                        address.thoroughfare,
                        address.subThoroughfare,
                        address.locality,
                        address.adminArea,
                        address.postalCode,
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error geocoding location")
                return Address("", "", "", "", "")
            }
        }
        return Address("", "", "", "", "")
    }

    @SuppressLint("VisibleForTests")
    private fun checkDeviceLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    turnOnLocationActivityResultLauncher.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Timber.tag(TAG)
                        .d("Error getting location settings resolution: %s", sendEx.message)
                }
            } else {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    R.string.location_required_error,
                    Snackbar.LENGTH_LONG,
                ).show()
            }
        }
        locationSettingsResponseTask.addOnSuccessListener {
            if (it.locationSettingsStates.isLocationPresent) {
                getLocation()
                hideKeyboard()
            }
        }
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}
