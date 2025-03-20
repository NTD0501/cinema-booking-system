package com.example.cinemabookingsystem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.example.cinemabookingsystem.adapter.RoomAdapter.RoomViewHolder
import com.example.cinemabookingsystem.databinding.ItemRoomBinding
import com.example.cinemabookingsystem.model.Room

class RoomAdapter(private val mListRooms: List<Room>?,
                  private val iManagerRoomListener: IManagerRoomListener) : RecyclerView.Adapter<RoomViewHolder>() {
    private var onBind = false

    interface IManagerRoomListener {
        fun clickItemRoom(room: Room)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val itemRoomBinding = ItemRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(itemRoomBinding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = mListRooms!![position]
        holder.mItemRoomBinding.tvTitle.text = room.title
        onBind = true
        holder.mItemRoomBinding.chbSelected.isChecked = room.isSelected
        onBind = false
        holder.mItemRoomBinding.chbSelected.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean ->
            if (!onBind) {
                iManagerRoomListener.clickItemRoom(room)
            }
        }
    }

    override fun getItemCount(): Int {
        return mListRooms?.size ?: 0
    }

    class RoomViewHolder(val mItemRoomBinding: ItemRoomBinding) : RecyclerView.ViewHolder(mItemRoomBinding.root)
}