package com.example.spms.ui.screen.patients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spms.data.model.Patient
import com.example.spms.data.repository.PatientRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PatientViewModel(
    private val db: FirebaseFirestore,
    private val patientRepo: PatientRepository
) : ViewModel() {

    private val _patients = MutableStateFlow<List<Patient>>(emptyList())
    val patients: StateFlow<List<Patient>> = _patients

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var listener: ListenerRegistration? = null

    fun startRealtime() {
        if (listener != null) return

        _loading.value = true
        listener = db.collection("patients")
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    _error.value = e.message
                    _loading.value = false
                    return@addSnapshotListener
                }

                val list = snap?.toObjects(Patient::class.java) ?: emptyList()
                _patients.value = list.sortedBy { it.code }
                _loading.value = false
            }
    }

    fun stopRealtime() {
        listener?.remove()
        listener = null
    }

    fun deletePatient(code: String, onDone: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                patientRepo.deletePatient(code)
                onDone(true, "data berhasil dihapus")
            } catch (e: Exception) {
                onDone(false, e.message ?: "gagal menghapus data")
            }
        }
    }
}