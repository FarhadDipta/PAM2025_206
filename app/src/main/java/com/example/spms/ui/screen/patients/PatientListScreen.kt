package com.example.spms.ui.screen.patients

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spms.data.model.Patient
import com.example.spms.ui.components.InfoDialog
import com.example.spms.ui.components.SuccessDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientListScreen(
    patients: List<Patient>,
    onBack: () -> Unit,
    onCreate: () -> Unit,
    onUpdate: (Patient) -> Unit,
    onDelete: (Patient) -> Unit
) {
    var selectedPatient by remember { mutableStateOf<Patient?>(null) }

    var showDetailDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreate,
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Pasien")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Data Pasien",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (patients.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada data pasien")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(patients, key = { it.code }) { patient ->
                        PatientCard(
                            patient = patient,
                            onCardClick = {
                                selectedPatient = patient
                                showDetailDialog = true
                            },
                            onUpdateClick = {
                                selectedPatient = patient
                                showUpdateDialog = true
                            },
                            onDeleteClick = {
                                selectedPatient = patient
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // =========================
    // DETAIL DIALOG (OK ONLY)
    // =========================
    if (showDetailDialog && selectedPatient != null) {
        val p = selectedPatient!!

        val detailText = """
            Detail Data Pasien
            
            Kode: ${p.code}
            Nama: ${p.name}
            NIK: ${p.nik}
            Jenis Kelamin: ${p.gender}
            No HP: ${p.phone}
            Nama Wali: ${p.guardianName}
        """.trimIndent()

        InfoDialog(
            title = "Detail Pasien",
            contentText = detailText,
            onDismiss = {
                showDetailDialog = false
                selectedPatient = null
            }
        )
    }

    // =========================
    // UPDATE CONFIRM DIALOG
    // =========================
    if (showUpdateDialog && selectedPatient != null) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            title = { Text("Konfirmasi Update") },
            text = { Text("Lanjutkan untuk mengupdate data ${selectedPatient!!.name}?") },
            confirmButton = {
                Button(onClick = {
                    showUpdateDialog = false
                    onUpdate(selectedPatient!!)
                }) {
                    Text("Submit")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showUpdateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // =========================
    // DELETE CONFIRM DIALOG
    // =========================
    if (showDeleteDialog && selectedPatient != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Yakin ingin menghapus ${selectedPatient!!.name}?") },
            confirmButton = {
                Button(onClick = {
                    showDeleteDialog = false
                    onDelete(selectedPatient!!)

                    // tampil success centang
                    successMessage = "Data pasien berhasil dihapus"
                    showSuccessDialog = true
                }) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // =========================
    // SUCCESS DIALOG (CENTANG)
    // =========================
    if (showSuccessDialog) {
        SuccessDialog(
            message = successMessage,
            onDismiss = { showSuccessDialog = false }
        )
    }
}

@Composable
private fun PatientCard(
    patient: Patient,
    onCardClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        onClick = onCardClick, // ✅ aman (tidak pakai Modifier.clickable)
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.10f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = patient.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text("Kode: ${patient.code}", style = MaterialTheme.typography.bodySmall)
            Text("NIK: ${patient.nik}", style = MaterialTheme.typography.bodySmall)
            Text("No HP: ${patient.phone}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onUpdateClick) { Text("Update") }
                TextButton(onClick = onDeleteClick) { Text("Delete") }
            }
        }
    }
}