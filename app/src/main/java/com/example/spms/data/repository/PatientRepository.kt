package com.example.spms.data.repository

import com.example.spms.data.model.Patient
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PatientRepository(
    private val db: FirebaseFirestore,
    private val counterRepo: CounterRepository
) {
    suspend fun getAllPatients(): List<Patient> {
        val snap = db.collection("patients").get().await()
        return snap.toObjects(Patient::class.java).sortedBy { it.code }
    }

    suspend fun createPatient(patient: Patient) {
        val code = counterRepo.nextPatientCode()
        val final = patient.copy(code = code)
        db.collection("patients").document(code).set(final).await()
    }

    suspend fun updatePatient(patient: Patient) {
        db.collection("patients").document(patient.code).set(patient).await()
    }

    suspend fun deletePatient(code: String) {
        db.collection("patients").document(code).delete().await()
    }

    suspend fun getPatientByCode(code: String): Patient? {
        val doc = db.collection("patients").document(code).get().await()
        return doc.toObject(Patient::class.java)
    }
}