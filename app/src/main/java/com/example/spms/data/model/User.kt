package com.example.spms.data.model

data class User(
    val email: String = "",
    val passwordHash: String = "",
    val role: String = "", // "ADMIN" atau "NURSE"
    val nurseCode: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)