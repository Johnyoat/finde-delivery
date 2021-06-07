package com.finde.deliveryapp.ui.newParcel


import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.ImagePickerMode
import com.esafirm.imagepicker.features.registerImagePicker
import com.finde.deliveryapp.R
import com.finde.deliveryapp.databinding.FragmentNewParcelBinding
import com.finde.deliveryapp.ext.areTextInputLayoutsValid
import com.finde.deliveryapp.ext.popStack
import com.finde.deliveryapp.models.BusinessModel
import com.finde.deliveryapp.models.DeliveryTimeLineModel
import com.finde.deliveryapp.models.ParcelModel
import com.finde.deliveryapp.models.UserModel
import com.finde.deliveryapp.ui.account.AccountViewModel
import com.finde.deliveryapp.ui.confirmation.ConfirmationFragment
import com.finde.deliveryapp.viewModels.ParcelsViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.auth.FirebaseAuth
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
class NewParcelFragment : Fragment() {


    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var rCode = 0
    private var parcel = ParcelModel()
    private lateinit var binding: FragmentNewParcelBinding
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private var fileUri: Uri? = null
    private val viewModel: NewParcelViewModel by viewModels()
    private val accountViewModel: AccountViewModel by viewModels()
    private var businesses = mutableListOf<BusinessModel>()
    private var isDestinationSelected = false
    private var isOriginSelected = false
    private lateinit var user: UserModel
    private val phoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()


    companion object {
        fun newInstance() = NewParcelFragment()
    }


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

    @DelicateCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parcels: ParcelsViewModel by activityViewModels()

        binding.parcelWeight.setItems("Light", "Medium", "Heavy")
        binding.itemType.setItems("Food/Drink", "Breakables", "Electronics", "Books")

        accountViewModel.getUser().observe(viewLifecycleOwner, {
            parcel.senderName = "${it.firstName} ${it.lastName}"
        })

        binding.parcelWeight.setOnItemSelectedListener { _, _, _, item ->
            parcel.parcelWeight = item.toString()

        }


        binding.itemType.setOnItemSelectedListener { _, _, _, item ->
            parcel.parcelType = item.toString()
        }


        binding.deliveryCompany.setOnItemSelectedListener { _, position, _, item ->
            parcel.deliveryCompanyName = item.toString()
            parcel.deliveryCompanyId = businesses[position].id.toString()
        }

        lat = arguments?.getDouble("lat")!!
        lng = arguments?.getDouble("lng")!!


        viewModel.getBusinesses().observe(viewLifecycleOwner, { businesses ->
            val businessList = mutableListOf<String>()
            this.businesses = businesses as MutableList<BusinessModel>
            businesses.forEach { business ->
                businessList.add(business.title)
            }
            binding.deliveryCompany.setItems(businessList)
        })


        val imagePicker = registerImagePicker {
            val image = it.firstOrNull() ?: return@registerImagePicker
            Glide.with(requireActivity()).load(image.uri).into(binding.packageImage)
            fileUri = image.uri
        }

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

            imagePicker.launch(ImagePickerConfig {
                isFolderMode = true
                isShowCamera = true
                limit = 1
                isIncludeVideo = false
                mode = ImagePickerMode.SINGLE
            })

        }

        binding.requestBiker.setOnClickListener {

            if (!isOriginSelected) {
                Snackbar.make(requireView(), "Origin not selected", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isDestinationSelected) {
                Snackbar.make(requireView(), "Destination not selected", Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (!areTextInputLayoutsValid(
                    binding.receiverContact,
                    binding.receiverName
                )
            ) return@setOnClickListener

            isProgressShown(true)

            parcel.receiverContact = binding.receiverContact.text.toString()
            parcel.receiverName = binding.receiverName.text.toString()
            parcel.origin = binding.origin.text.toString()
            parcel.destination = binding.destination.text.toString()
            parcel.senderContact = phoneNumber
            parcel.createdAt = System.currentTimeMillis()
            parcel.timeline.add(DeliveryTimeLineModel("Order Created Successfully",System.currentTimeMillis(),true))

            try {
                GlobalScope.launch {
                    if (fileUri !== null) {
                        parcel.packageImage = uploadFile(fileUri!!, parcel.id, parcel.uid)
                    }
                    parcels.add(parcel).await()


                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Parcel Request Added", Toast.LENGTH_SHORT)
                            .show()
                        popStack()
                        ConfirmationFragment(true,parcel.id).show(childFragmentManager,"CF")
                    }

                }


            } catch (e: Exception) {
                Timber.d(e.localizedMessage)
                ConfirmationFragment(false,parcel.id).show(childFragmentManager,"CF")
                isProgressShown(false)
            }


        }
    }

    private fun isProgressShown(status: Boolean): Unit {
        binding.requestBiker.isEnabled = !status
        binding.toolbar.progressBar.isVisible = status
    }


    private val placePickerResultContracts =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { results ->
            if (results.resultCode == Activity.RESULT_OK) {
                val carmenFeature =
                    PlacePicker.getPlace(results.data) ?: return@registerForActivityResult
                val coordinates = carmenFeature.center()!!.coordinates()
                print(carmenFeature.placeName())
                when (rCode) {
                    1 -> {
                        origin.text = carmenFeature.text()
                        parcel.originLat = coordinates[0]
                        parcel.originLng = coordinates[1]
                        isOriginSelected = true
                    }
                    2 -> {
                        destination.text = carmenFeature.text()
                        parcel.destinationLat = coordinates[0]
                        parcel.destinationLng = coordinates[1]
                        isDestinationSelected = true
                    }
                }
            }
        }

    private fun goToPickerActivity() {

        placePickerResultContracts.launch(

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
                .build(activity)
        )

    }


    private suspend fun uploadFile(uri: Uri, fileId: String, uid: String): String {
        val ref = storageRef.child("deliveries/${uid}/${fileId}")
        return ref.putFile(uri).await().storage.downloadUrl.await().toString()
    }

}
