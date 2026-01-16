package com.example.spms.data.model

data class Patient(
    val code: String = "",
    val name: String = "",
    val nik: String = "",
    val gender: String = "",
    val phone: String = "",
    val guardianName: String = "",
    val createdAt: Long = System.currentTimeMillis()
)