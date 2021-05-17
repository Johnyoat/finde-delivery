package com.finde.deliveryapp.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.finde.deliveryapp.R
import com.finde.deliveryapp.adapters.RecentParcelAdapter
import com.finde.deliveryapp.databinding.HistoryFragmentBinding
import com.finde.deliveryapp.viewModels.ParcelsViewModel

class HistoryFragment : DialogFragment() {

    companion object {
        fun newInstance() = HistoryFragment()
    }

    private val viewModel: ParcelsViewModel by viewModels()
    private lateinit var binding:HistoryFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HistoryFragmentBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.backBtn.setOnClickListener {
            dismissAllowingStateLoss()
        }

        viewModel.getHistory().observe(viewLifecycleOwner,{ parcelLists ->

            binding.emptyLayout.isVisible = parcelLists.isEmpty()
            binding.recentParcelsList.isVisible = parcelLists.isNotEmpty()

            binding.recentParcelsList.apply {
                adapter = RecentParcelAdapter(parcelLists,requireActivity() as AppCompatActivity)
            }
        })

    }


    override fun getTheme(): Int {
        return R.style.AppTheme
    }



}