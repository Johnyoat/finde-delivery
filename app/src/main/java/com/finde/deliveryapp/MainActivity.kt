package com.finde.deliveryapp

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.finde.deliveryapp.adapters.RecentParcelAdapter
import com.finde.deliveryapp.api.APIService
import com.finde.deliveryapp.models.BusinessModel
import com.finde.deliveryapp.models.ParcelModel
import com.finde.deliveryapp.ui.NewParcelFragment
import com.finde.deliveryapp.viewModels.ParcelsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.Property
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.parcels_ui.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity(), PermissionsListener {

    private var maps: MapboxMap? = null
    private lateinit var mapView: MapView
    private var locationEngine: LocationEngine? = null
    private val defaultIntervalSeconds = 1000L
    private val defaultMaxWaitTime = defaultIntervalSeconds * 5
    private val callback = LocationCallBack(this)
    private var locationComponent: LocationComponent? = null
    private var location: Location? = null
    private var symbolManager: SymbolManager? = null
    private val BUSINESS_ICON = "business_icon"
//    private var symbol:Symbol? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Mapbox.getInstance(this, getString(R.string.map_box_token))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)




        mapView.getMapAsync { mapboxMap ->
            mapboxMap.uiSettings.isAttributionEnabled = false
            maps = mapboxMap
            maps!!.setStyle(
                Style.MAPBOX_STREETS
            ) { style: Style? ->
                style?.let {

                    val bitmap = ContextCompat.getDrawable(this, R.drawable.ic_location_pin)?.toBitmap()
                    it.addImage(BUSINESS_ICON, bitmap!!)
                    symbolManager = SymbolManager(mapView, maps!!, it)
                    this.enableLocationComponent(it)
                    getBusinesses(symbolManager)
                }
            }
        }





        val recentParcelList = findViewById<RecyclerView>(R.id.recentParcelList)
        val parcels: ParcelsViewModel by viewModels()
        val parcelSheet = BottomSheetBehavior.from(parcelBottomSheet)
        parcelSheet.peekHeight = resources.getDimensionPixelSize(R.dimen.recentParcelPeekHeightMinimized)
        locationBtn.layoutParams = setLayoutParams(R.dimen.fabMarginBottomMinimized)

        val dummyData = mutableListOf<ParcelModel>()
        dummyData.add(
            ParcelModel(
                receiverName = "Kafui Richard",
                origin = "Santasi",
                destination = "Patasi"
            )
        )
        dummyData.add(
            ParcelModel(
                receiverName = "Eric Osei",
                origin = "Adum",
                destination = "Tech Junction"
            )
        )

        parcels.setParcel(dummyData)

        parcels.getParcels().observe(this, Observer { parcelx ->

            if (parcelx == null || parcelx.size > 0) {
                parcelSheet.peekHeight = resources.getDimensionPixelSize(R.dimen.recentParcelPeekHeight)
                locationBtn.layoutParams = setLayoutParams(R.dimen.fabMarginBottom)
                parcelx.reverse()
                recentParcelList.adapter = RecentParcelAdapter(parcelx, this)
            }
        })


        minimize.setOnClickListener {
            parcelSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        }


        sendParcelFab.setOnClickListener {
            callNewParcel()
        }

        parcelSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(view: View, offset: Float) {
                val alpha = 1 - offset
                sendParcel.alpha = alpha
                parcelTitle.alpha = offset
                minimize.alpha = offset

            }

            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(view: View, state: Int) {
                when (state) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        sendParcel.visibility = View.GONE
                        parcelTitle.visibility = View.VISIBLE
                        minimize.visibility = View.VISIBLE
                    }

                    BottomSheetBehavior.STATE_DRAGGING -> {
                        parcelTitle.visibility = View.VISIBLE
                        minimize.visibility = View.VISIBLE
                        sendParcel.visibility = View.VISIBLE
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        sendParcel.visibility = View.VISIBLE
                        parcelTitle.visibility = View.GONE
                        minimize.visibility = View.GONE
                    }
                }
            }
        })


        locationBtn.setOnClickListener {
            getLocation(locationComponent)
        }

        sendParcel.setOnClickListener {
            callNewParcel()
        }
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
                        .setPositiveButton("Close",null)
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
            val newParcel = NewParcelFragment(location!!.latitude, location!!.longitude)
            newParcel.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme)
            newParcel.show(supportFragmentManager, "")
        } else {
            Toast.makeText(this, "Turn on Location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLayoutParams(marginBottom: Int): RelativeLayout.LayoutParams {
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        layoutParams.setMargins(
            0,
            0,
            resources.getDimensionPixelSize(R.dimen.sixteen),
            resources.getDimensionPixelSize(marginBottom)
        )
        return layoutParams

    }

    private fun enableLocationComponent(style: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(applicationContext)) {
            locationComponent = maps!!.locationComponent
            // Set the LocationComponent activation options
            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(applicationContext, style)
                    .useDefaultLocationEngine(false)
                    .build()
            // Activate with the LocationComponentActivationOptions object
            locationComponent?.activateLocationComponent(locationComponentActivationOptions)
            locationComponent?.isLocationComponentEnabled = true
            locationComponent?.cameraMode = CameraMode.TRACKING
            locationComponent?.renderMode = RenderMode.COMPASS
            maps!!.setMaxZoomPreference(14.0)
            initLocationEngine()
        } else { //User Location and MapInit
            val permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }

    private fun moveCamera(latLng: LatLng) {
        val position = CameraPosition.Builder().target(latLng)
            .zoom(14.0) //                .tilt(30) // Set the camera tilt
            .build()
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(position)
        maps!!.animateCamera(cameraUpdate)
    }

    @SuppressLint("MissingPermission")
    private fun initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(applicationContext)
        val request =
            LocationEngineRequest.Builder(defaultIntervalSeconds)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(defaultMaxWaitTime).build()
        locationEngine?.requestLocationUpdates(request, callback, Looper.getMainLooper())
        locationEngine?.getLastLocation(callback)
    }


    private fun getLocation(locationComponent: LocationComponent?) {
        if (locationComponent != null) {
            val location = locationComponent.lastKnownLocation
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                moveCamera(latLng)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        if (locationEngine != null) {
            locationEngine!!.removeLocationUpdates(callback)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }


    override fun onExplanationNeeded(permissionsToExplain: List<String?>?) {
        Toast.makeText(
            applicationContext,
            getString(R.string.user_location_permission_explanation),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (!granted) {
            Toast.makeText(
                applicationContext,
                R.string.user_location_permission_not_granted,
                Toast.LENGTH_LONG
            ).show()
        }
    }


    private class LocationCallBack(activity: MainActivity) :
        LocationEngineCallback<LocationEngineResult> {
        private val activityWeakReference: WeakReference<MainActivity> = WeakReference(activity)
        private var isAlreadyMoved = false
        override fun onSuccess(result: LocationEngineResult) {
            val activity: MainActivity? = activityWeakReference.get()
            if (activity != null) {
                val location = result.lastLocation ?: return
                // Pass the new location to the Maps SDK's LocationComponent
                if (activity.maps != null && result.lastLocation != null) {
                    activity.maps!!.locationComponent
                        .forceLocationUpdate(result.lastLocation)
                    if (!isAlreadyMoved){
                        activity.moveCamera(LatLng(location.latitude, location.longitude))
                        isAlreadyMoved = true
                    }


                    activity.location = location
                }
            }
        }

        override fun onFailure(exception: Exception) {}

    }

}
