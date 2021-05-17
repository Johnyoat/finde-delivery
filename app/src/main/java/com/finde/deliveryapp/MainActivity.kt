package com.finde.deliveryapp

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.finde.deliveryapp.api.APIService
import com.finde.deliveryapp.databinding.ActivityMainUiBinding
import com.finde.deliveryapp.models.BusinessModel
import com.finde.deliveryapp.ui.NewParcelFragment
import com.finde.deliveryapp.ui.account.AccountFragment
import com.finde.deliveryapp.ui.history.HistoryFragment
import com.finde.deliveryapp.ui.qrscanner.QRScannerFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import com.mapbox.android.core.location.*
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.parcels_ui.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*


class MainActivity : AppCompatActivity() {


    private var locationComponent: LocationComponent? = null
    private var location: Location? = null
    private var symbolManager: SymbolManager? = null
    private val BUSINESS_ICON = "business_icon"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val isCameraGranted = false
    private val isLocationGrated = false

    //    private var symbol:Symbol? = null
    private lateinit var binding: ActivityMainUiBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        Mapbox.getInstance(this, getString(R.string.map_box_token))
        super.onCreate(savedInstanceState)
        binding = ActivityMainUiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        val dialogMultiplePermissionsListener: MultiplePermissionsListener =
            DialogOnAnyDeniedMultiplePermissionsListener.Builder
                .withContext(applicationContext)
                .withTitle("Action Required")
                .withMessage("The app needs both camera,location to work properly")
                .withButtonText("Okay")
                .build()


        Dexter.withContext(applicationContext)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(dialogMultiplePermissionsListener)


        binding.sendPackage.setOnClickListener {
            callNewParcel()
        }

        binding.history.setOnClickListener {
            HistoryFragment().show(supportFragmentManager, "HT")
        }

        binding.scanPackage.setOnClickListener {
            QRScannerFragment().show(supportFragmentManager, "SP")
        }

        binding.userProfile.setOnClickListener {
            AccountFragment().show(supportFragmentManager, "Ac")
        }

        getUserLocation()
    }

    private fun getBusinesses(symbolManager: SymbolManager?) {
        val apiService = APIService.create()
        apiService.getBusinesses().enqueue(object : Callback<List<BusinessModel>> {
            override fun onResponse(
                call: Call<List<BusinessModel>>,
                response: Response<List<BusinessModel>>
            ) {
                val symbolOptionList = mutableListOf<SymbolOptions>()

                response.body()?.forEach { businessModel ->
                    symbolOptionList.add(
                        SymbolOptions().withLatLng(
                            LatLng(
                                businessModel.geolocation.lat,
                                businessModel.geolocation.lng
                            )
                        ).withIconImage(BUSINESS_ICON)
                            .withIconSize(1.3f)
                            .withTextField(businessModel.title)
                            .withTextOpacity(0f)

                    )

                }

                symbolManager?.create(symbolOptionList)

                symbolManager?.addClickListener { symbol ->
                    MaterialAlertDialogBuilder(this@MainActivity)
                        .setMessage(symbol.textField)
                        .setPositiveButton("Close", null)
                        .show()
                    false
                }
            }

            override fun onFailure(call: Call<List<BusinessModel>>, t: Throwable) {
                Timber.d(t.localizedMessage)
            }

        })
    }

    private fun callNewParcel() {
        if (location != null) {
            val newParcel = NewParcelFragment(location!!.latitude, location!!.longitude,)
            newParcel.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme)
            newParcel.show(supportFragmentManager, "")
        } else {
            Toast.makeText(this, "Turn on Location", Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        if (!isCameraGranted){
            DialogOnDeniedPermissionListener.Builder
                .withContext(applicationContext)
                .withTitle("Location Permission")
                .withMessage("Location permission is required for the app to work properly")
                .withButtonText("Allow")
                .withIcon(R.drawable.ic_location_pin)
                .build()
        }


        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            this.location = location
            if (location == null) {
                binding.location.text = "Location Disabled"
                return@addOnSuccessListener
            }
            val address = Geocoder(applicationContext, Locale.getDefault()).getFromLocation(
                location.latitude,
                location.longitude,
                1
            )[0]

            binding.location.text = "${address.getAddressLine(0)}"

        }

    }


    private fun getLocation(locationComponent: LocationComponent?) {
        if (locationComponent != null) {
            val location = locationComponent.lastKnownLocation
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
            }
        }
    }


}
