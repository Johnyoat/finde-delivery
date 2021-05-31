package com.finde.deliveryapp.ui.newParcel


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.finde.deliveryapp.R
import com.finde.deliveryapp.databinding.FragmentNewParcelBinding
import com.finde.deliveryapp.ext.popStack
import com.finde.deliveryapp.models.ParcelModel
import com.finde.deliveryapp.viewModels.ParcelsViewModel
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker
import com.mapbox.mapboxsdk.plugins.places.picker.model.PlacePickerOptions
import kotlinx.android.synthetic.main.fragment_new_parcel.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber

/**
 * A simple [Fragment] subclass.
 */
class NewParcelFragment() : DialogFragment() {


    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var rCode = 0
    private var cameraCode = 11
    private var parcel = ParcelModel()
    private lateinit var binding: FragmentNewParcelBinding
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private var fileUri: Uri? = null
    private val viewModel: NewParcelViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewParcelBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parcels: ParcelsViewModel by activityViewModels()

        parcelWeight.setItems("Light", "Medium", "Heavy")
        itemType.setItems("Food/Drink", "Breakables", "Electronics", "Books")

        lat = arguments?.getDouble("lat")!!
        lng = arguments?.getDouble("lng")!!


        binding.toolbar.backBtn.setOnClickListener {
           popStack()
        }

        binding.origin.setOnClickListener {
            rCode = 1
            goToPickerActivity()
        }

        binding.destination.setOnClickListener {
            rCode = 2
            goToPickerActivity()
        }


        binding.capturePackage.setOnClickListener {
            ImagePicker.create(requireActivity())
                .folderMode(true)
                .single()
                .showCamera(true)
                .start(cameraCode)
        }

        binding.requestBiker.setOnClickListener {

            binding.requestBiker.isEnabled = false

            binding.toolbar.progressBar.isVisible = true

            parcel.receiverContact = binding.receiverContact.text.toString()
            parcel.receiverName = binding.receiverName.text.toString()
            parcel.origin = binding.origin.text.toString()
            parcel.destination = binding.destination.text.toString()



            try {
                GlobalScope.launch {
                    if (fileUri !== null) {
                        parcel.packageImage = uploadFile(fileUri!!, parcel.id)
                    }
                    parcels.add(parcel).await()


                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Parcel Request Added", Toast.LENGTH_SHORT)
                            .show()
                        dismissAllowingStateLoss()
                    }

                }


            } catch (e: Exception) {
                Timber.d(e.localizedMessage)
                Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_SHORT).show()
            }

            binding.requestBiker.isEnabled = true
            binding.toolbar.progressBar.isVisible = false

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
                .build(activity), rCode
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_CANCELED || data == null) return


        when (requestCode) {
            rCode -> {
                val carmenFeature = PlacePicker.getPlace(data) ?: return
                val coordinates = carmenFeature.center()!!.coordinates()
                print(carmenFeature.placeName())
                when (rCode) {
                    1 -> {
                        origin.text = carmenFeature.text()
                        parcel.originLat = coordinates[0]
                        parcel.originLng = coordinates[1]
                    }
                    2 -> {
                        destination.text = carmenFeature.text()
                        parcel.destinationLat = coordinates[0]
                        parcel.destinationLng = coordinates[1]
                    }
                }
            }

            cameraCode -> {
                if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
                    val image = ImagePicker.getFirstImageOrNull(data)
                    Glide.with(requireActivity()).load(image.uri).into(binding.packageImage)
                    fileUri = image.uri
                }
            }

        }

    }

    private suspend fun uploadFile(uri: Uri, fileId: String): String {
        val ref = storageRef.child("deliveries/${fileId}")
        return ref.putFile(uri).await().storage.downloadUrl.await().toString()
    }

}
