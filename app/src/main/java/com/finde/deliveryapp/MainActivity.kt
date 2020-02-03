package com.finde.deliveryapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.finde.deliveryapp.ui.NewParcelFragment
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.parcels_ui.*

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        Mapbox.getInstance(this, getString(R.string.map_box_token))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync { mapboxMap ->
            mapboxMap.uiSettings.isAttributionEnabled = false


            mapboxMap.setStyle(
                Style.MAPBOX_STREETS
            ) { }
        }

        parcel.setOnClickListener {
            startActivity(Intent(this, DeliveryActivity::class.java))
        }
        parcel2.setOnClickListener {
            startActivity(Intent(this, DeliveryActivity::class.java))
        }

        sendParcel.setOnClickListener {
            val newParcel = NewParcelFragment()
            newParcel.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme)
            newParcel.show(supportFragmentManager, "")
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
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}
