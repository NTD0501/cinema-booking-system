package com.example.cinemabookingsystem.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cinemabookingsystem.fragment.SettingFragment
import com.example.cinemabookingsystem.fragment.PromotionFragment
import com.example.cinemabookingsystem.fragment.HomeFragment

class MyViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> PromotionFragment()
            2 -> SettingFragment()
            else -> HomeFragment()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}