package com.example.spms.data.repository

import com.example.spms.data.model.Nurse
import com.example.spms.data.model.User
import com.example.spms.utils.HashUtil
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class NurseRepository(
    private val db: FirebaseFirestore,
    private val counterRepo: CounterRepository
) {
    suspend fun getAllNurses(): List<Nurse> {
        val snap = db.collection("nurses").get().await()
        return snap.toObjects(Nurse::class.java).sortedBy { it.code }
    }

    suspend fun createNurse(nurse: Nurse, passwordPlain: String) {
        // cek email sudah dipakai di users atau belum
        val userCheck = db.collection("users")
            .whereEqualTo("email", nurse.email)
            .limit(1)
            .get()
            .await()
        if (!userCheck.isEmpty) {
            throw Exception("Email sudah terdaftar, gunakan email lain.")
        }

        val code = counterRepo.nextNurseCode()
        val nurseFinal = nurse.copy(code = code)

        // Simpan data perawat
        db.collection("nurses").document(code).set(nurseFinal).await()

        // Simpan akun login perawat
        val user = User(
            email = nurseFinal.email,
            passwordHash = HashUtil.sha256(passwordPlain),
            role = "NURSE",
            nurseCode = code
        )
        db.collection("users").add(user).await()
    }

    suspend fun updateNurse(nurse: Nurse) {
        // Ambil data lama dulu (untuk cek perubahan email)
        val oldDoc = db.collection("nurses").document(nurse.code).get().await()
        val old = oldDoc.toObject(Nurse::class.java)

        // Update data perawat
        db.collection("nurses").document(nurse.code).set(nurse).await()

        // Jika email berubah → update juga di users berdasarkan nurseCode
        val oldEmail = old?.email ?: ""
        if (oldEmail.isNotBlank() && oldEmail != nurse.email) {
            val q = db.collection("users")
                .whereEqualTo("nurseCode", nurse.code)
                .limit(1)
                .get()
                .await()

            if (!q.isEmpty) {
                q.documents.first().reference.update("email", nurse.email).await()
            }
        }
    }

    suspend fun deleteNurse(nurseCode: String) {
        // hapus data nurse
        db.collection("nurses").document(nurseCode).delete().await()

        // hapus akun user yang nurseCode sama
        val q = db.collection("users").whereEqualTo("nurseCode", nurseCode).get().await()
        for (doc in q.documents) {
            doc.reference.delete().await()
        }
    }

    suspend fun getNurseByCode(code: String): Nurse? {
        val doc = db.collection("nurses").document(code).get().await()
        return doc.toObject(Nurse::class.java)
    }
}