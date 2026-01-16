package com.example.spms.data.model

data class Nurse(
    val code: String = "",
    val name: String = "",
    val nip: String = "",
    val gender: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val createdAt: Long = System.currentTimeMillis()
)