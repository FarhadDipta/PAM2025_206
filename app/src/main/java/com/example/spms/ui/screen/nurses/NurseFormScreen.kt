package com.example.spms.ui.screen.nurses

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spms.data.model.Nurse
import com.example.spms.data.repository.NurseRepository
import com.example.spms.ui.components.GenderDropdown
import com.example.spms.ui.components.PreviewDialog
import com.example.spms.utils.Validators
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NurseFormScreen(
    nurseRepo: NurseRepository,
    onBack: () -> Unit,
    initialNurse: Nurse? = null
) {
    val isUpdate = initialNurse != null

    var name by remember { mutableStateOf(initialNurse?.name ?: "") }
    var nip by remember { mutableStateOf(initialNurse?.nip ?: "") }
    var gender by remember { mutableStateOf(initialNurse?.gender ?: "") }
    var phone by remember { mutableStateOf(initialNurse?.phone ?: "") }
    var email by remember { mutableStateOf(initialNurse?.email ?: "") }
    var address by remember { mutableStateOf(initialNurse?.address ?: "") }

    // Password hanya untuk CREATE (admin input password perawat)
    var password by remember { mutableStateOf("") }

    var showPreview by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    val isFormFilled = name.isNotBlank() && nip.isNotBlank() && gender.isNotBlank() &&
            phone.isNotBlank() && email.isNotBlank() && address.isNotBlank() &&
            (isUpdate || password.isNotBlank())

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (isUpdate) "Update Perawat" else "Tambah Perawat") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            OutlinedTextField(name, { name = it }, label = { Text("Nama") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(nip, { nip = it }, label = { Text("NIP / STR / SIP") }, modifier = Modifier.fillMaxWidth())

            GenderDropdown(
                value = gender,
                onValueChange = { gender = it },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(phone, { phone = it }, label = { Text("No HP") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(address, { address = it }, label = { Text("Alamat") }, modifier = Modifier.fillMaxWidth())

            if (!isUpdate) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password Perawat (input oleh admin)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    error = null

                    if (!Validators.isValidPhone(phone.trim())) {
                        error = "data No HP salah, perbaiki dengan format 08xxxxxxxxxx (10-13 digit)"
                        return@Button
                    }
                    if (!Validators.isValidEmail(email.trim())) {
                        error = "data Email salah, perbaiki dengan format email yang benar"
                        return@Button
                    }
                    if (!isUpdate && password.trim().length < 6) {
                        error = "data Password salah, minimal 6 karakter"
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
            NIP/STR/SIP: ${nip.trim()}
            Jenis Kelamin: ${gender.trim()}
            No HP: ${phone.trim()}
            Email: ${email.trim()}
            Alamat: ${address.trim()}
        """.trimIndent()

        PreviewDialog(
            title = "Preview Data Perawat",
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
                            nurseRepo.updateNurse(
                                Nurse(
                                    code = initialNurse!!.code,
                                    name = name.trim(),
                                    nip = nip.trim(),
                                    gender = gender.trim(),
                                    phone = phone.trim(),
                                    email = email.trim(),
                                    address = address.trim(),
                                    createdAt = initialNurse.createdAt
                                )
                            )
                            snackbarHostState.showSnackbar("data berhasil diupdate")
                        } else {
                            nurseRepo.createNurse(
                                Nurse(
                                    name = name.trim(),
                                    nip = nip.trim(),
                                    gender = gender.trim(),
                                    phone = phone.trim(),
                                    email = email.trim(),
                                    address = address.trim()
                                ),
                                passwordPlain = password.trim()
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