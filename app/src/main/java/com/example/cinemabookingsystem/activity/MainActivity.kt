package com.example.cinemabookingsystem.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.example.cinemabookingsystem.R
import com.example.cinemabookingsystem.adapter.MyViewPagerAdapter
import com.example.cinemabookingsystem.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        val myViewPagerAdapter = MyViewPagerAdapter(this)
        activityMainBinding.viewpager2.adapter = myViewPagerAdapter
        activityMainBinding.viewpager2.isUserInputEnabled = false
        activityMainBinding.viewpager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        activityMainBinding.bottomNavigation.menu.findItem(R.id.nav_home).isChecked = true
                        activityMainBinding.tvTitle.text = getString(R.string.nav_home)
                    }
                    1 -> {
                        activityMainBinding.bottomNavigation.menu.findItem(R.id.nav_booking).isChecked = true
                        activityMainBinding.tvTitle.text = getString(R.string.nav_booking)
                    }
                    2 -> {
                        activityMainBinding.bottomNavigation.menu.findItem(R.id.nav_user).isChecked = true
                        activityMainBinding.tvTitle.text = getString(R.string.nav_user)
                    }
                }
            }
        })
        activityMainBinding.bottomNavigation.setOnItemSelectedListener { item: MenuItem ->
            val id = item.itemId
            if (id == R.id.nav_home) {
                activityMainBinding.viewpager2.currentItem = 0
                activityMainBinding.tvTitle.text = getString(R.string.nav_home)
            } else if (id == R.id.nav_booking) {
                activityMainBinding.viewpager2.currentItem = 1
                activityMainBinding.tvTitle.text = getString(R.string.nav_booking)
            } else if (id == R.id.nav_user) {
                activityMainBinding.viewpager2.currentItem = 2
                activityMainBinding.tvTitle.text = getString(R.string.nav_user)
            }
            true
        }
    }

    private fun showDialogLogout() {
        MaterialDialog.Builder(this)
                .title(getString(R.string.app_name))
                .content(getString(R.string.msg_confirm_login_another_device))
                .positiveText(getString(R.string.action_ok))
                .negativeText(getString(R.string.action_cancel))
                .onPositive { dialog: MaterialDialog, _: DialogAction? ->
                    dialog.dismiss()
                    finishAffinity()
                }
                .onNegative { dialog: MaterialDialog, _: DialogAction? -> dialog.dismiss() }
                .cancelable(true)
                .show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        showDialogLogout()
    }

}