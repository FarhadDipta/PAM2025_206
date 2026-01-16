package com.example.spms.ui.screen.nurses

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spms.data.model.Nurse
import com.example.spms.data.repository.NurseRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NurseUpdateScreen(
    nurseCode: String,
    nurseRepo: NurseRepository,
    onBack: () -> Unit
) {
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var nurse by remember { mutableStateOf<Nurse?>(null) }

    LaunchedEffect(nurseCode) {
        loading = true
        error = null
        try {
            nurse = nurseRepo.getNurseByCode(nurseCode)
            if (nurse == null) error = "Data perawat tidak ditemukan"
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
                    title = { Text("Update Perawat") },
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
                    title = { Text("Update Perawat") },
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

    NurseFormScreen(
        nurseRepo = nurseRepo,
        onBack = onBack,
        initialNurse = nurse
    )
}