package com.finde.deliveryapp.ui.homeFragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.finde.deliveryapp.R
import com.finde.deliveryapp.databinding.HomeFragmentBinding
import com.finde.deliveryapp.ext.load
import com.finde.deliveryapp.ext.navigateTo
import com.finde.deliveryapp.ext.navigateToWithArgs
import com.finde.deliveryapp.ui.account.AccountViewModel
import com.finde.deliveryapp.ui.editProfile.EditProfileFragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.transition.MaterialFadeThrough
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.util.*

class HomeFragment : Fragment() {

    private var location: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel: HomeViewModel by viewModels()
    private val accountViewModel: AccountViewModel by viewModels()
    private lateinit var binding: HomeFragmentBinding

    private val locationRequestContract =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            requestLocation()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        accountViewModel.getUser().observe(viewLifecycleOwner, { user ->
            if (user == null) {
                EditProfileFragment().show(childFragmentManager, "PF")
            } else {
                binding.userProfile.load(requireContext(), user.profileUrl)
            }
        })


        binding.sendPackage.setOnClickListener {
            callNewParcel()
        }

        binding.history.setOnClickListener {
            navigateTo(R.id.historyFragment)
        }

        binding.scanPackage.setOnClickListener {
            navigateTo(R.id.QRScannerFragment)
        }

        binding.userProfile.setOnClickListener {
            navigateTo(R.id.accountFragment)
        }
    }


    private fun callNewParcel() {
        if (location != null) {
            navigateToWithArgs(
                R.id.newParcelFragment,
                bundleOf("lng" to location!!.longitude, "lat" to location!!.latitude)
            )
        } else {
            Toast.makeText(requireContext(), "Turn on Location", Toast.LENGTH_SHORT).show()
        }
    }


    private fun checkAndRequestLocation(): Unit {
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(permission: PermissionGrantedResponse?) {
                    requestLocation()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {

                }

                override fun onPermissionRationaleShouldBeShown(
                    request: PermissionRequest?,
                    token: PermissionToken?
                ) {

                }
            }).check()
    }


    @SuppressLint("MissingPermission")
    private fun getUserLocation(locationRequest: LocationRequest) {

        locationRequest.interval = 2 * 1000

        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)

                for (location in result.locations){
                    this@HomeFragment.location = location
                    if (location == null) {
                        binding.location.text = "Location Disabled"
                        return
                    }
                    val address = Geocoder(requireContext(), Locale.getDefault()).getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    )[0]
                    binding.location.text = "${address.getAddressLine(0)}"

                    fusedLocationClient.removeLocationUpdates(this)
                }

            }
        }, Looper.getMainLooper())

    }


    private fun requestLocation() {
        this.let {
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            val task = LocationServices.getSettingsClient(requireActivity())
                .checkLocationSettings(builder.build())

            task.addOnSuccessListener { response ->
                val states = response.locationSettingsStates!!
                if (states.isLocationPresent) {
                    getUserLocation(locationRequest)
                }
            }

            task.addOnFailureListener { e ->
                if (e is ResolvableApiException) {
                    try {
                        locationRequestContract.launch(
                            IntentSenderRequest.Builder(e.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                    }
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        checkAndRequestLocation()
    }

}