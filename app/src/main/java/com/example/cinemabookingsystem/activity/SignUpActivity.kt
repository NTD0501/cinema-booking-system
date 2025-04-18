package com.example.cinemabookingsystem.activity

import android.os.Bundle
import android.widget.Toast
import com.example.cinemabookingsystem.R
import com.example.cinemabookingsystem.constant.ConstantKey
import com.example.cinemabookingsystem.constant.GlobalFunction
import com.example.cinemabookingsystem.databinding.ActivitySignUpBinding
import com.example.cinemabookingsystem.model.*
import com.example.cinemabookingsystem.prefs.DataStoreManager
import com.example.cinemabookingsystem.util.StringUtil
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : BaseActivity() {

    private var mActivitySignUpBinding: ActivitySignUpBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivitySignUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(mActivitySignUpBinding!!.root)
        mActivitySignUpBinding!!.imgBack.setOnClickListener { onBackPressed() }
        mActivitySignUpBinding!!.layoutSignIn.setOnClickListener { finish() }
        mActivitySignUpBinding!!.btnSignUp.setOnClickListener { onClickValidateSignUp() }
    }

    private fun onClickValidateSignUp() {
        val strEmail = mActivitySignUpBinding!!.edtEmail.text.toString().trim { it <= ' ' }
        val strPassword = mActivitySignUpBinding!!.edtPassword.text.toString().trim { it <= ' ' }
        val strFullname = mActivitySignUpBinding!!.edtFullname.text.toString().trim { it <= ' ' }

        if (StringUtil.isEmpty(strEmail)) {
            Toast.makeText(this@SignUpActivity, getString(R.string.msg_email_require), Toast.LENGTH_SHORT).show()
        } else if (StringUtil.isEmpty(strPassword)) {
            Toast.makeText(this@SignUpActivity, getString(R.string.msg_password_require), Toast.LENGTH_SHORT).show()
        } else if (StringUtil.isEmpty(strFullname)) {
            Toast.makeText(this@SignUpActivity, getString(R.string.msg_fullname_require), Toast.LENGTH_SHORT).show()
        } else if (!StringUtil.isValidEmail(strEmail)) {
            Toast.makeText(this@SignUpActivity, getString(R.string.msg_email_invalid), Toast.LENGTH_SHORT).show()
        } else {

                signUpUser(strEmail, strPassword, strFullname)

        }
    }

    private fun signUpUser(email: String, password: String, fullname: String) {
        showProgressDialog(true)
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                showProgressDialog(false)
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        val userObject = User(user.email, password, fullname)
                        if (user.email != null && user.email!!.contains(ConstantKey.ADMIN_EMAIL_FORMAT)) {
                            userObject.isAdmin = true
                        }

                        // Lưu thông tin người dùng vào Firebase Realtime Database
                        val database = FirebaseDatabase.getInstance()
                        val myRef = database.getReference("users")
                        myRef.child(user.uid).setValue(userObject)
                            .addOnSuccessListener {
                                DataStoreManager.setUser(userObject)
                                GlobalFunction.gotoMainActivity(this)
                                finishAffinity()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this@SignUpActivity, "Error saving user data: ${e.message}",
                                    Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this@SignUpActivity, getString(R.string.msg_sign_up_error),
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}