package com.finde.deliveryapp.ui


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.finde.deliveryapp.R
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker
import com.mapbox.mapboxsdk.plugins.places.picker.model.PlacePickerOptions
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class NewParcelFragment(private val lat: Double, private val lng:Double) : DialogFragment() {


    private var rCode = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_parcel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val back: AppCompatImageButton = view.findViewById(R.id.back)
        back.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun goToPickerActivity() {
        startActivityForResult(
            PlacePicker.IntentBuilder()
                .accessToken(getString(R.string.map_box_token))
                .placeOptions(
                    PlacePickerOptions.builder()
                        .statingCameraPosition(
                            CameraPosition.Builder()
                                .target(LatLng(lat, lng)).zoom(16.0).build()
                        )
                        .build()
                )
                .build(activity), rCode)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == rCode && resultCode == Activity.RESULT_OK) {
            if (data == null) return
            val carmenFeature = PlacePicker.getPlace(data) ?: return
            val cordinates = carmenFeature.center()?.coordinates() as ArrayList<Double?>
        }
    }

}
