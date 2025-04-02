
package com.example.cinemabookingsystem.model

import com.google.gson.Gson

class User {
    var email: String? = null
    var password: String? = null
    var fullname: String? = null
    var isAdmin = false

    constructor() {}
    constructor(email: String?, password: String?) {
        this.email = email
        this.password = password
        this.fullname = ""
    }
    constructor(email: String?, password: String?, fullname: String?) {
        this.email = email
        this.password = password
        this.fullname = fullname
    }

    fun toJSon(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}