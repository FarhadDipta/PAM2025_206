package com.example.spms.ui.screen.patients

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spms.data.model.Patient
import com.example.spms.data.repository.PatientRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientUpdateScreen(
    patientCode: String,
    patientRepo: PatientRepository,
    onBack: () -> Unit
) {
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var patient by remember { mutableStateOf<Patient?>(null) }

    LaunchedEffect(patientCode) {
        loading = true
        error = null
        try {
            patient = patientRepo.getPatientByCode(patientCode)
            if (patient == null) error = "Data pasien tidak ditemukan"
        } catch (e: Exception) {
            error = e.message
        } finally {
            loading = false
        }
    }

    if (loading) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Update Pasien") },
                    navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
                )
            }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        return
    }

    if (error != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Update Pasien") },
                    navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
                )
            }
        ) { padding ->
            Column(Modifier.padding(padding).padding(16.dp)) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }
        }
        return
    }

    // kalau sukses ambil data, pakai PatientFormScreen dengan initialPatient
    PatientFormScreen(
        patientRepo = patientRepo,
        onBack = onBack,
        initialPatient = patient
    )
}