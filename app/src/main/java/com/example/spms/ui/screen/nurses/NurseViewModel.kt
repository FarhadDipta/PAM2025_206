package com.example.spms.ui.screen.nurses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spms.data.model.Nurse
import com.example.spms.data.repository.NurseRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NurseViewModel(
    private val db: FirebaseFirestore,
    private val nurseRepo: NurseRepository
) : ViewModel() {

    private val _nurses = MutableStateFlow<List<Nurse>>(emptyList())
    val nurses: StateFlow<List<Nurse>> = _nurses

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var listener: ListenerRegistration? = null

    fun startRealtime() {
        if (listener != null) return

        _loading.value = true
        listener = db.collection("nurses")
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    _error.value = e.message
                    _loading.value = false
                    return@addSnapshotListener
                }

                val list = snap?.toObjects(Nurse::class.java) ?: emptyList()
                _nurses.value = list.sortedBy { it.code }
                _loading.value = false
            }
    }

    fun stopRealtime() {
        listener?.remove()
        listener = null
    }

    fun deleteNurse(code: String, onDone: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                nurseRepo.deleteNurse(code)
                onDone(true, "data berhasil dihapus")
            } catch (e: Exception) {
                onDone(false, e.message ?: "gagal menghapus data")
            }
        }
    }
}