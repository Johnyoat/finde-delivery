package com.finde.deliveryapp.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.finde.deliveryapp.databinding.AccountFragmentBinding
import com.finde.deliveryapp.ext.load
import com.finde.deliveryapp.ext.popStack
import com.finde.deliveryapp.ui.LoginSplashActivity
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.auth.FirebaseAuth


class AccountFragment : Fragment() {
    private lateinit var binding: AccountFragmentBinding


    private  val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AccountFragmentBinding.inflate(layoutInflater,container,false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.backBtn.setOnClickListener {
            popStack()
        }

        binding.toolbar.title.text = "Account"

        viewModel.getUser().observe(viewLifecycleOwner,{user ->

            binding.userProfile.load(requireContext(),user.profileUrl)
            binding.username.text = "${user.firstName} ${user.lastName}"
            binding.userPhoneNumber.text = user.phoneNumber
        })


        binding.logOut.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            requireActivity().startActivity( Intent(requireContext(),LoginSplashActivity::class.java))
            requireActivity().finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
    }


}