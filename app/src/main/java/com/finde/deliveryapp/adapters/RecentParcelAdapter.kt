package com.finde.deliveryapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.finde.deliveryapp.DeliveryActivity
import com.finde.deliveryapp.R
import com.finde.deliveryapp.ext.navigateToWithArgs
import com.finde.deliveryapp.models.ParcelModel

class RecentParcelAdapter(
    private val parcels: List<ParcelModel>,
    private val activity: AppCompatActivity
) :
    RecyclerView.Adapter<RecentParcelAdapter.ParcelViewHolder>() {


    class ParcelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiverName: AppCompatTextView = itemView.findViewById(R.id.receiverName)
        val origin: AppCompatTextView = itemView.findViewById(R.id.origin)
        val destination: AppCompatTextView = itemView.findViewById(R.id.destination)
        val packageImage: AppCompatImageView = itemView.findViewById(R.id.packageImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParcelViewHolder {
        return ParcelViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.parcel_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return parcels.size
    }

    override fun onBindViewHolder(holder: ParcelViewHolder, position: Int) {
        val parcel = parcels[position]
        val receiverName = "To : ${parcel.receiverName}"
        holder.receiverName.text = receiverName
        holder.origin.text = parcel.origin
        holder.destination.text = parcel.destination
        if (parcel.packageImage.isNotEmpty()){
            Glide.with(activity).load(parcel.packageImage).into(holder.packageImage)
        }

        holder.itemView.setOnClickListener {
            activity.navigateToWithArgs(R.id.trackingFragment, bundleOf("parcel" to parcel))
        }
    }
}