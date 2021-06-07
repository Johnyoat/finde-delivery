package com.finde.deliveryapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.finde.deliveryapp.databinding.ItemDeliveryTimelineBinding
import com.finde.deliveryapp.ext.convertValueToDateFormat
import com.finde.deliveryapp.models.DeliveryTimeLineModel
import kotlinx.android.synthetic.main.item_delivery_timeline.view.*

class DeliveryTimelineAdapter(
    private val deliveryTimelines
    : List<DeliveryTimeLineModel>
) : RecyclerView.Adapter<DeliveryTimelineAdapter.DeliveryTimelineViewHolder>() {


    inner class DeliveryTimelineViewHolder(itemView: ItemDeliveryTimelineBinding) : RecyclerView.ViewHolder(itemView.root) {
        fun setUpData(deliveryTimeLineModel: DeliveryTimeLineModel): Unit {
            itemView.timelineDate.text = deliveryTimeLineModel.createdAt.convertValueToDateFormat("hh:mm a, dd-MMM-yyyy")
            itemView.timelineTitle.text = deliveryTimeLineModel.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryTimelineViewHolder {
        return DeliveryTimelineViewHolder(ItemDeliveryTimelineBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: DeliveryTimelineViewHolder, position: Int) {
        holder.setUpData(deliveryTimelines[position])
    }

    override fun getItemCount() = deliveryTimelines.size
}