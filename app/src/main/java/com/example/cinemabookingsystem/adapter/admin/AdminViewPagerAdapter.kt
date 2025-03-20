package com.example.cinemabookingsystem.adapter.admin

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cinemabookingsystem.fragment.admin.*

class AdminViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> AdminFoodFragment()
            2 -> AdminHomeFragment()
            3 -> AdminBookingFragment()
            4 -> AdminManageFragment()
            else -> AdminCategoryFragment()
        }
    }

    override fun getItemCount(): Int {
        return 5
    }
}