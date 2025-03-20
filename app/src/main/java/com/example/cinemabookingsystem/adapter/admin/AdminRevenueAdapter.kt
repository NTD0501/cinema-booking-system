package com.example.cinemabookingsystem.adapter.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cinemabookingsystem.adapter.admin.AdminRevenueAdapter.AdminRevenueViewHolder
import com.example.cinemabookingsystem.constant.ConstantKey
import com.example.cinemabookingsystem.databinding.ItemRevenueBinding
import com.example.cinemabookingsystem.model.Revenue

class AdminRevenueAdapter(private val mListRevenue: List<Revenue>?) : RecyclerView.Adapter<AdminRevenueViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminRevenueViewHolder {
        val itemRevenueBinding = ItemRevenueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdminRevenueViewHolder(itemRevenueBinding)
    }

    override fun onBindViewHolder(holder: AdminRevenueViewHolder, position: Int) {
        val revenue = mListRevenue!![position]
        holder.mItemRevenueBinding.tvStt.text = (position + 1).toString()
        holder.mItemRevenueBinding.tvMovieName.text = revenue.movieName
        holder.mItemRevenueBinding.tvQuantity.text = revenue.quantity.toString()
        val total = revenue.totalPrice.toString() + ConstantKey.UNIT_CURRENCY
        holder.mItemRevenueBinding.tvTotalPrice.text = total
    }

    override fun getItemCount(): Int {
        return mListRevenue?.size ?: 0
    }

    class AdminRevenueViewHolder(val mItemRevenueBinding: ItemRevenueBinding) : RecyclerView.ViewHolder(mItemRevenueBinding.root)
}