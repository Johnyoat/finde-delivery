package com.finde.deliveryapp.ui.newParcel

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.finde.deliveryapp.R
import com.finde.deliveryapp.databinding.FragmentNewParcelDeliveryCompanySearchBinding
import com.finde.deliveryapp.databinding.ItemDeliveryCompanyBinding
import com.finde.deliveryapp.models.BusinessModel
import java.util.*


class NewParcelDeliveryCompanySearchFragment(private var companies: MutableList<BusinessModel>) :
    DialogFragment() {
    private lateinit var binding: FragmentNewParcelDeliveryCompanySearchBinding
    private val parcelViewModel: NewParcelViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentNewParcelDeliveryCompanySearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val companyListAdapter = CompanyListAdapter()

        binding.deliveryCompanyList.apply {
            adapter = companyListAdapter
        }

        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            companyListAdapter.filter.filter(text)
        }


        binding.backBtn.setOnClickListener {
            dismissAllowingStateLoss()
        }

    }


    override fun getTheme(): Int {
        return R.style.AppTheme
    }


    inner class CompanyListAdapter :
        RecyclerView.Adapter<CompanyListAdapter.CompanyListViewHolder>(), Filterable {
        private val unfilteredData = companies

        inner class CompanyListViewHolder(private val item: ItemDeliveryCompanyBinding) :
            RecyclerView.ViewHolder(item.root) {

            fun setUpData(company: BusinessModel) {
                item.companyName.text = company.title
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyListViewHolder {
            return CompanyListViewHolder(
                ItemDeliveryCompanyBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ),
                )
            )
        }

        override fun onBindViewHolder(holder: CompanyListViewHolder, position: Int) {
            holder.setUpData(companies[position])
            holder.itemView.setOnClickListener {
                parcelViewModel.setUpDeliveryCompany(companies[position])
                dismissAllowingStateLoss()
            }
        }

        override fun getItemCount() = companies.size

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val filteredList = mutableListOf<BusinessModel>()
                    if (constraint?.isEmpty() == true) {
                        companies = unfilteredData
                    } else {
                        for (company in companies) {
                            if (company.toString().toLowerCase(Locale.ROOT).trim()
                                    .contains(constraint.toString().toLowerCase(Locale.ROOT).trim())
                            ) {
                                filteredList.add(company)
                            }
                        }

                        companies = filteredList
                    }

                    val results = FilterResults()
                    results.values = companies
                    return results
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    companies = results?.values as MutableList<BusinessModel>
                    notifyDataSetChanged()
                }

            }
        }
    }
}