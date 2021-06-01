package com.finde.deliveryapp.ui.editProfile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.ImagePickerMode
import com.esafirm.imagepicker.features.registerImagePicker
import com.finde.deliveryapp.R
import com.finde.deliveryapp.databinding.FragmentEditProfileBinding
import com.finde.deliveryapp.ext.load
import com.finde.deliveryapp.models.UserModel
import com.finde.deliveryapp.ui.account.AccountViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class EditProfileFragment : DialogFragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private val accountViewModel: AccountViewModel by viewModels()
    private lateinit var userModel: UserModel
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private var fileUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @DelicateCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountViewModel.getUser().observe(viewLifecycleOwner, { user ->
            binding.firstName.editText?.setText(user.firstName)
            binding.lastName.editText?.setText(user.lastName)
            binding.phoneNumber.editText?.setText(user.phoneNumber)
            binding.userProfile.load(requireContext(), user.profileUrl)
            userModel = user
        })


        val imagePicker = registerImagePicker {
            val image = it.firstOrNull() ?: return@registerImagePicker
            Glide.with(requireActivity()).load(image.uri).into(binding.userProfile)
            fileUri = image.uri
        }


        binding.pickImageBtn.setOnClickListener {
            imagePicker.launch(ImagePickerConfig {
                isFolderMode = true
                isShowCamera = true
                limit = 1
                isIncludeVideo = false
                mode = ImagePickerMode.SINGLE
            })
        }


        binding.saveBtn.setOnClickListener {
            userModel.firstName = binding.firstName.editText?.text.toString()
            userModel.lastName = binding.lastName.editText?.text.toString()
            userModel.phoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()

            binding.progressBar.isGone = false
            binding.saveBtn.isEnabled = false
            try {
                GlobalScope.launch {
                    if (fileUri !== null) {
                        userModel.profileUrl = uploadFile(fileUri!!, userModel.uid)
                    }
                    accountViewModel.updateUser(userModel).await()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "User info updated", Toast.LENGTH_SHORT)
                            .show()
                        dismissAllowingStateLoss()
                    }

                }


            } catch (e: Exception) {
                Timber.d(e.localizedMessage)
                binding.saveBtn.isEnabled = true
                binding.progressBar.isGone = true

            }
        }

        binding.closeBtnImg.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

    private suspend fun uploadFile(uri: Uri, uid: String): String {
        val ref = storageRef.child("users/${uid}")
        return ref.putFile(uri).await().storage.downloadUrl.await().toString()
    }

    override fun getTheme(): Int {
        return R.style.AppTheme
    }


}