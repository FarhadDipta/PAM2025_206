package com.example.spms.ui.screen.patients

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spms.data.model.Patient
import com.example.spms.data.repository.PatientRepository
import com.example.spms.ui.components.GenderDropdown
import com.example.spms.ui.components.PreviewDialog
import com.example.spms.utils.Validators
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientFormScreen(
    patientRepo: PatientRepository,
    onBack: () -> Unit,
    initialPatient: Patient? = null
) {
    val isUpdate = initialPatient != null

    var name by remember { mutableStateOf(initialPatient?.name ?: "") }
    var nik by remember { mutableStateOf(initialPatient?.nik ?: "") }
    var gender by remember { mutableStateOf(initialPatient?.gender ?: "") }
    var phone by remember { mutableStateOf(initialPatient?.phone ?: "") }
    var guardian by remember { mutableStateOf(initialPatient?.guardianName ?: "") }

    var showPreview by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    val isFormFilled = name.isNotBlank() && nik.isNotBlank() && gender.isNotBlank() &&
            phone.isNotBlank() && guardian.isNotBlank()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (isUpdate) "Update Pasien" else "Tambah Pasien") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = nik,
                onValueChange = { nik = it },
                label = { Text("NIK / Nomor Identitas") },
                modifier = Modifier.fillMaxWidth()
            )

            GenderDropdown(
                value = gender,
                onValueChange = { gender = it },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("No HP (Pasien/Wali)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = guardian,
                onValueChange = { guardian = it },
                label = { Text("Nama Orang Tua / Wali") },
                modifier = Modifier.fillMaxWidth()
            )

            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    error = null

                    if (!Validators.isValidNik(nik.trim())) {
                        error = "data NIK salah, perbaiki dengan memasukan 16 digit angka"
                        return@Button
                    }
                    if (!Validators.isValidPhone(phone.trim())) {
                        error = "data No HP salah, perbaiki dengan format 08xxxxxxxxxx (10-13 digit)"
                        return@Button
                    }

                    showPreview = true
                },
                enabled = isFormFilled && !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lanjut Preview")
            }
        }
    }

    if (showPreview) {
        val previewText = """
            Data yang ingin ${if (isUpdate) "diupdate" else "ditambahkan"}:
            
            Nama: ${name.trim()}
            NIK: ${nik.trim()}
            Jenis Kelamin: ${gender.trim()}
            No HP: ${phone.trim()}
            Nama Wali: ${guardian.trim()}
        """.trimIndent()

        PreviewDialog(
            title = "Preview Data Pasien",
            contentText = previewText,
            onCancel = {
                showPreview = false
                CoroutineScope(Dispatchers.Main).launch {
                    snackbarHostState.showSnackbar("data tidak berhasil ${if (isUpdate) "diupdate" else "ditambahkan"}")
                }
            },
            onSubmit = {
                loading = true
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        if (isUpdate) {
                            patientRepo.updatePatient(
                                Patient(
                                    code = initialPatient!!.code,
                                    name = name.trim(),
                                    nik = nik.trim(),
                                    gender = gender.trim(),
                                    phone = phone.trim(),
                                    guardianName = guardian.trim(),
                                    createdAt = initialPatient.createdAt
                                )
                            )
                            snackbarHostState.showSnackbar("data berhasil diupdate")
                        } else {
                            patientRepo.createPatient(
                                Patient(
                                    name = name.trim(),
                                    nik = nik.trim(),
                                    gender = gender.trim(),
                                    phone = phone.trim(),
                                    guardianName = guardian.trim()
                                )
                            )
                            snackbarHostState.showSnackbar("data berhasil ditambahkan")
                        }

                        showPreview = false
                        loading = false
                        onBack()

                    } catch (e: Exception) {
                        loading = false
                        showPreview = false
                        snackbarHostState.showSnackbar(e.message ?: "gagal menyimpan data")
                    }
                }
            }
        )
    }
}