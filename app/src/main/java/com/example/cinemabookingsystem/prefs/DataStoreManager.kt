package com.example.cinemabookingsystem.prefs

import android.content.Context
import com.example.cinemabookingsystem.model.User
import com.example.cinemabookingsystem.prefs.MySharedPreferences
import com.example.cinemabookingsystem.util.StringUtil.isEmpty
import com.google.gson.Gson

class DataStoreManager {
    private var sharedPreferences: MySharedPreferences? = null

    companion object {
        private const val PREF_USER_INFOR = "PREF_USER_INFOR"
        private var instance: DataStoreManager? = null

        fun init(context: Context?) {
            instance = DataStoreManager()
            instance!!.sharedPreferences = MySharedPreferences(context)
        }

        fun getInstance(): DataStoreManager? {
            return if (instance != null) {
                instance
            } else {
                throw IllegalStateException("Not initialized")
            }
        }

        fun setUser(user: User?) {
            val jsonUser = user?.toJSon() ?: ""
            getInstance()?.sharedPreferences?.putStringValue(PREF_USER_INFOR, jsonUser)
        }

        fun getUser(): User? {
            val jsonUser = getInstance()?.sharedPreferences?.getStringValue(PREF_USER_INFOR)
            return if (!isEmpty(jsonUser)) {
                Gson().fromJson(jsonUser, User::class.java)
            } else null
        }
    }
}