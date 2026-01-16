package com.example.spms.data.repository

import com.example.spms.data.model.User
import com.example.spms.utils.HashUtil
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val db: FirebaseFirestore
) {
    suspend fun login(email: String, passwordPlain: String): User? {
        val usersRef = db.collection("users")

        val q = usersRef.whereEqualTo("email", email).limit(1).get().await()
        if (q.isEmpty) return null

        val doc = q.documents.first()
        val user = doc.toObject(User::class.java) ?: return null

        val inputHash = HashUtil.sha256(passwordPlain)
        return if (inputHash == user.passwordHash) user else null
    }
}