package com.finde.deliveryapp

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.finde.deliveryapp.adapters.RecentParcelAdapter
import com.finde.deliveryapp.models.ParcelModel
import com.finde.deliveryapp.ui.NewParcelFragment
import com.finde.deliveryapp.viewModels.ParcelsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.parcels_ui.*
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
                style?.let { this.enableLocationComponent(it) }
            }
        }



        val recentParcelList = findViewById<RecyclerView>(R.id.recentParcelList)
        val parcels: ParcelsViewModel by viewModels()
        val parcelSheet = BottomSheetBehavior.from(parcelBottomSheet)
        parcelSheet.peekHeight = resources.getDimensionPixelSize(R.dimen.minimizeHeight)
        locationBtn.layoutParams = setLayoutParams(R.dimen.tenZeroFour)

        val dummyData =  mutableListOf<ParcelModel>()
        dummyData.add(ParcelModel(receiverName = "Kafui Richard", origin = "Santasi" , destination = "Patasi"))
        dummyData.add(ParcelModel(receiverName = "Eric Osei", origin = "Adum" , destination = "Tech Junction"))

        parcels.setParcel(dummyData)

        parcels.getParcels().observe(this, Observer { parcelx ->

            if (parcelx == null || parcelx.size > 0) {
                parcelSheet.peekHeight = resources.getDimensionPixelSize(R.dimen.fullHeight)
                locationBtn.layoutParams = setLayoutParams(R.dimen.threeZeroFour)
                recentParcelList.adapter = RecentParcelAdapter(parcelx, this)
            }
        })



        locationBtn.setOnClickListener {
            getLocation(locationComponent)
        }

        sendParcel.setOnClickListener {
            if (location != null) {
                val newParcel = NewParcelFragment(location!!.latitude, location!!.longitude)
                newParcel.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme)
                newParcel.show(supportFragmentManager, "")
            }
        }
    }

    private fun setLayoutParams(marginBottom: Int): RelativeLayout.LayoutParams{
        val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        layoutParams.setMargins(0,0,resources.getDimensionPixelSize(R.dimen.sixteen),resources.getDimensionPixelSize(marginBottom))
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


    private class LocationCallBack internal constructor(activity: MainActivity) :
        LocationEngineCallback<LocationEngineResult> {
        private val activityWeakReference: WeakReference<MainActivity> = WeakReference(activity)
        override fun onSuccess(result: LocationEngineResult) {
            val activity: MainActivity? = activityWeakReference.get()
            if (activity != null) {
                val location = result.lastLocation ?: return
                // Pass the new location to the Maps SDK's LocationComponent
                if (activity.maps != null && result.lastLocation != null) {
                    activity.maps!!.locationComponent
                        .forceLocationUpdate(result.lastLocation)
                    activity.moveCamera(LatLng(location.latitude, location.longitude))
                    activity.location = location
                }
            }
        }

        override fun onFailure(exception: Exception) {}

    }

}
