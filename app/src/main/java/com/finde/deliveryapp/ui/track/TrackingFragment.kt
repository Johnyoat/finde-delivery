package com.finde.deliveryapp.ui.track

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.finde.deliveryapp.R
import com.finde.deliveryapp.databinding.TrackingFragmentBinding
import com.google.android.material.transition.MaterialFadeThrough

class TrackingFragment : DialogFragment() {

    companion object {
        fun newInstance() = TrackingFragment()
    }

    private val viewModel: TrackingViewModel by viewModels()
    private lateinit var binding:TrackingFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = TrackingFragmentBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeBtn.setOnClickListener {
            dismissAllowingStateLoss()
        }


    }


    override fun getTheme(): Int {
        return R.style.AppTheme
    }

}