package com.finde.deliveryapp.ui.account

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.finde.deliveryapp.R
import com.finde.deliveryapp.databinding.AccountFragmentBinding
import com.finde.deliveryapp.ui.LoginSplashActivity
import com.google.android.material.transition.MaterialFadeThrough
import com.google.firebase.auth.FirebaseAuth
import come.finde.finderider.ui.account.AccountViewModel


class AccountFragment : DialogFragment() {
    private lateinit var binding: AccountFragmentBinding

    companion object {
        fun newInstance() = AccountFragment()
    }

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
            dismissAllowingStateLoss()
        }

        binding.toolbar.title.text = "Account"


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


    override fun getTheme(): Int {
        return R.style.AppTheme
    }

}