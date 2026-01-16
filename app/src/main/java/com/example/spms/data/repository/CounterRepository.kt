package com.example.spms.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CounterRepository(
    private val db: FirebaseFirestore
) {
    suspend fun nextNurseCode(): String {
        val ref = db.collection("counters").document("nurseCounter")

        return db.runTransaction { trx ->
            val snap = trx.get(ref)
            val last = snap.getLong("lastNumber") ?: 0L
            val newNumber = last + 1
            trx.set(ref, mapOf("lastNumber" to newNumber)) // set biar aman kalau belum ada
            "PRW" + newNumber.toString().padStart(3, '0')
        }.await()
    }

    suspend fun nextPatientCode(): String {
        val ref = db.collection("counters").document("patientCounter")

        return db.runTransaction { trx ->
            val snap = trx.get(ref)
            val last = snap.getLong("lastNumber") ?: 0L
            val newNumber = last + 1
            trx.set(ref, mapOf("lastNumber" to newNumber))
            "PSN" + newNumber.toString().padStart(3, '0')
        }.await()
    }
}