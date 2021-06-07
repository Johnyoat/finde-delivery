package com.finde.deliveryapp.ui.track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.finde.deliveryapp.R
import com.finde.deliveryapp.adapters.DeliveryTimelineAdapter
import com.finde.deliveryapp.databinding.TrackingFragmentBinding
import com.finde.deliveryapp.ext.convertValueToDateFormat
import com.finde.deliveryapp.ext.popStack
import com.finde.deliveryapp.models.ParcelModel

class TrackingFragment : DialogFragment() {


    private val viewModel: TrackingViewModel by viewModels()
    private lateinit var binding: TrackingFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = TrackingFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parcelModel = requireArguments().getParcelable<ParcelModel>("parcel")!!

        setUpParcel(parcelModel)

        binding.closeBtn.setOnClickListener {
            popStack()
        }

    }

    private fun setUpParcel(parcelModel: ParcelModel) {
        binding.orderId.text = HtmlCompat.fromHtml(
            "Order Id: <b>#${parcelModel.id}</b>",
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
        binding.orderDate.text = parcelModel.createdAt.convertValueToDateFormat("EEE, MMM dd,yyyy")
        binding.eta.text = "ETA: ${parcelModel.ETA}"
        binding.deliveryTimeline.apply {
            adapter = DeliveryTimelineAdapter(parcelModel.timeline)
        }
    }


    override fun getTheme(): Int {
        return R.style.AppTheme
    }

}