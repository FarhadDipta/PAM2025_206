package com.example.spms.ui.screen.nurses

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
import com.example.spms.data.model.Nurse
import com.example.spms.ui.components.InfoDialog
import com.example.spms.ui.components.SuccessDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NurseListScreen(
    nurses: List<Nurse>,
    onBack: () -> Unit,
    onCreate: () -> Unit,
    onUpdate: (Nurse) -> Unit,
    onDelete: (Nurse) -> Unit
) {
    var selectedNurse by remember { mutableStateOf<Nurse?>(null) }

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
                Icon(Icons.Default.Add, contentDescription = "Tambah Perawat")
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
                text = "Data Perawat",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (nurses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada data perawat")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(nurses, key = { it.code }) { nurse ->
                        NurseCard(
                            nurse = nurse,
                            onCardClick = {
                                selectedNurse = nurse
                                showDetailDialog = true
                            },
                            onUpdateClick = {
                                selectedNurse = nurse
                                showUpdateDialog = true
                            },
                            onDeleteClick = {
                                selectedNurse = nurse
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // =========================
    // DETAIL (OK ONLY)
    // =========================
    if (showDetailDialog && selectedNurse != null) {
        val n = selectedNurse!!

        val detailText = """
            Detail Data Perawat
            
            Kode: ${n.code}
            Nama: ${n.name}
            NIP/STR/SIP: ${n.nip}
            Jenis Kelamin: ${n.gender}
            No HP: ${n.phone}
            Email: ${n.email}
            Alamat: ${n.address}
        """.trimIndent()

        InfoDialog(
            title = "Detail Perawat",
            contentText = detailText,
            onDismiss = {
                showDetailDialog = false
                selectedNurse = null
            }
        )
    }

    // =========================
    // UPDATE CONFIRM
    // =========================
    if (showUpdateDialog && selectedNurse != null) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            title = { Text("Konfirmasi Update") },
            text = { Text("Lanjutkan untuk mengupdate data ${selectedNurse!!.name}?") },
            confirmButton = {
                Button(onClick = {
                    showUpdateDialog = false
                    onUpdate(selectedNurse!!)
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
    // DELETE CONFIRM
    // =========================
    if (showDeleteDialog && selectedNurse != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Yakin ingin menghapus ${selectedNurse!!.name}?") },
            confirmButton = {
                Button(onClick = {
                    showDeleteDialog = false
                    onDelete(selectedNurse!!)

                    successMessage = "Data perawat berhasil dihapus"
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
    // SUCCESS (CENTANG)
    // =========================
    if (showSuccessDialog) {
        SuccessDialog(
            message = successMessage,
            onDismiss = { showSuccessDialog = false }
        )
    }
}

@Composable
private fun NurseCard(
    nurse: Nurse,
    onCardClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        onClick = onCardClick, // ✅ aman
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.10f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = nurse.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text("Kode: ${nurse.code}", style = MaterialTheme.typography.bodySmall)
            Text("Email: ${nurse.email}", style = MaterialTheme.typography.bodySmall)
            Text("No HP: ${nurse.phone}", style = MaterialTheme.typography.bodySmall)

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