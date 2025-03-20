package com.example.cinemabookingsystem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cinemabookingsystem.R
import com.example.cinemabookingsystem.adapter.SeatAdapter.SeatViewHolder
import com.example.cinemabookingsystem.databinding.ItemSeatBinding
import com.example.cinemabookingsystem.model.SeatLocal

class SeatAdapter(private val mListSeats: List<SeatLocal>?,
                  private val iManagerSeatListener: IManagerSeatListener) : RecyclerView.Adapter<SeatViewHolder>() {
    interface IManagerSeatListener {
        fun clickItemSeat(seat: SeatLocal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val itemSeatBinding = ItemSeatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeatViewHolder(itemSeatBinding)
    }

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        val seat = mListSeats!![position]
        if (seat.isSelected) {
            holder.mItemSeatBinding.layoutItem.setBackgroundResource(R.drawable.bg_seat_not_avaiable_corner_5)
        } else {
            if (seat.isChecked) {
                holder.mItemSeatBinding.layoutItem.setBackgroundResource(R.drawable.bg_seat_selected_corner_5)
            } else {
                holder.mItemSeatBinding.layoutItem.setBackgroundResource(R.drawable.bg_seat_avaiable_corner_5)
            }
        }
        holder.mItemSeatBinding.tvTitle.text = seat.title
        holder.mItemSeatBinding.layoutItem.setOnClickListener { iManagerSeatListener.clickItemSeat(seat) }
    }

    override fun getItemCount(): Int {
        return mListSeats?.size ?: 0
    }

    class SeatViewHolder(val mItemSeatBinding: ItemSeatBinding) : RecyclerView.ViewHolder(mItemSeatBinding.root)
}