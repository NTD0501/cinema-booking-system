package com.example.cinemabookingsystem.activity

import android.os.Bundle
import android.widget.Toast
import com.example.cinemabookingsystem.R
import com.example.cinemabookingsystem.constant.ConstantKey
import com.example.cinemabookingsystem.constant.GlobalFunction
import com.example.cinemabookingsystem.databinding.ActivitySignInBinding
import com.example.cinemabookingsystem.model.*
import com.example.cinemabookingsystem.prefs.DataStoreManager
import com.example.cinemabookingsystem.util.StringUtil
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignInActivity : BaseActivity() {

    private var mActivitySignInBinding: ActivitySignInBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivitySignInBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(mActivitySignInBinding!!.root)
        mActivitySignInBinding!!.layoutSignUp.setOnClickListener {
            GlobalFunction.startActivity(this@SignInActivity, SignUpActivity::class.java) }
        mActivitySignInBinding!!.btnSignIn.setOnClickListener { onClickValidateSignIn() }
        mActivitySignInBinding!!.tvForgotPassword.setOnClickListener { onClickForgotPassword() }
    }

    private fun onClickForgotPassword() {
        GlobalFunction.startActivity(this, ForgotPasswordActivity::class.java)
    }

    private fun onClickValidateSignIn() {
        val strEmail = mActivitySignInBinding!!.edtEmail.text.toString().trim { it <= ' ' }
        val strPassword = mActivitySignInBinding!!.edtPassword.text.toString().trim { it <= ' ' }
        if (StringUtil.isEmpty(strEmail)) {
            Toast.makeText(this@SignInActivity, getString(R.string.msg_email_require), Toast.LENGTH_SHORT).show()
        } else if (StringUtil.isEmpty(strPassword)) {
            Toast.makeText(this@SignInActivity, getString(R.string.msg_password_require), Toast.LENGTH_SHORT).show()
        } else if (!StringUtil.isValidEmail(strEmail)) {
            Toast.makeText(this@SignInActivity, getString(R.string.msg_email_invalid), Toast.LENGTH_SHORT).show()
        } else {
            signInUser(strEmail, strPassword)
        }
    }

    private fun signInUser(email: String, password: String) {
        showProgressDialog(true)
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        val database = FirebaseDatabase.getInstance()
                        val userRef = database.getReference("users").child(user.uid)

                        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                showProgressDialog(false)

                                if (snapshot.exists()) {
                                    // Get user data including fullname from database
                                    val userData = snapshot.getValue(User::class.java)

                                    if (userData != null) {
                                        // Use the retrieved data but update with current password
                                        userData.password = password

                                        // Check and set admin status
                                        if (user.email != null && user.email!!.contains(ConstantKey.ADMIN_EMAIL_FORMAT)) {
                                            userData.isAdmin = true
                                        }

                                        // Store user data and navigate
                                        DataStoreManager.setUser(userData)
                                        GlobalFunction.gotoMainActivity(this@SignInActivity)
                                        finishAffinity()
                                    } else {
                                        // Fallback if user data is null
                                        handleMissingUserData(user, password)
                                    }
                                } else {
                                    // User exists in Authentication but not in Database
                                    handleMissingUserData(user, password)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                showProgressDialog(false)
                                Toast.makeText(this@SignInActivity,
                                    "Error retrieving user data: ${error.message}",
                                    Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                } else {
                    showProgressDialog(false)
                    Toast.makeText(this@SignInActivity, getString(R.string.msg_sign_in_error),
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Helper function for handling missing user data cases
    private fun handleMissingUserData(user: FirebaseUser, password: String) {
        val userObject = User(user.email, password)
        if (user.email != null && user.email!!.contains(ConstantKey.ADMIN_EMAIL_FORMAT)) {
            userObject.isAdmin = true
        }
        DataStoreManager.setUser(userObject)
        GlobalFunction.gotoMainActivity(this)
        finishAffinity()
    }
}