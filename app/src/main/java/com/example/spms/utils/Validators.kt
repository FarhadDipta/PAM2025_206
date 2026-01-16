package com.example.spms.utils

import android.util.Patterns

object Validators {

    fun isValidPhone(phone: String): Boolean {
        if (!phone.startsWith("08")) return false
        if (phone.length !in 10..13) return false
        return phone.all { it.isDigit() }
    }

    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidNik(nik: String): Boolean {
        return nik.length == 16 && nik.all { it.isDigit() }
    }
}