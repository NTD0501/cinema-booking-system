package com.example.cinemabookingsystem.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cinemabookingsystem.activity.ChangePasswordActivity
import com.example.cinemabookingsystem.activity.SignInActivity
import com.example.cinemabookingsystem.activity.admin.AdminRevenueActivity
import com.example.cinemabookingsystem.constant.GlobalFunction.startActivity
import com.example.cinemabookingsystem.databinding.FragmentAdminManageBinding
import com.example.cinemabookingsystem.prefs.DataStoreManager
import com.google.firebase.auth.FirebaseAuth

class AdminManageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentAdminManageBinding = FragmentAdminManageBinding.inflate(inflater, container, false)
        fragmentAdminManageBinding.tvEmail.text = DataStoreManager.getUser()?.email
        fragmentAdminManageBinding.layoutReport.setOnClickListener { onClickReport() }
        fragmentAdminManageBinding.layoutSignOut.setOnClickListener { onClickSignOut() }
        fragmentAdminManageBinding.layoutChangePassword.setOnClickListener { onClickChangePassword() }
        return fragmentAdminManageBinding.root
    }

    private fun onClickReport() {
        startActivity(activity, AdminRevenueActivity::class.java)
    }

    private fun onClickChangePassword() {
        startActivity(activity, ChangePasswordActivity::class.java)
    }

    private fun onClickSignOut() {
        if (activity == null) {
            return
        }
        FirebaseAuth.getInstance().signOut()
        DataStoreManager.setUser(null)
        startActivity(activity, SignInActivity::class.java)
        activity!!.finishAffinity()
    }
}