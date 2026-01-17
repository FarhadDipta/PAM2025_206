package com.example.spms.ui.screen.patients

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
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

private enum class PatientSortOption(val label: String) {
    NAME_ASC("Nama (A-Z)"),
    NAME_DESC("Nama (Z-A)"),
    CODE_ASC("Kode (A-Z)"),
    CODE_DESC("Kode (Z-A)"),
    NEWEST("Terbaru"),
    OLDEST("Terlama")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientListScreen(
    patients: List<Patient>,
    onBack: () -> Unit,
    onCreate: () -> Unit,
    onUpdate: (Patient) -> Unit,
    onDelete: (Patient, (Boolean, String) -> Unit) -> Unit
) {
    var selectedPatient by remember { mutableStateOf<Patient?>(null) }

    var showPreviewDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Search
    var searchQuery by remember { mutableStateOf("") }

    // Sort
    var sortExpanded by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf(PatientSortOption.NEWEST) }

    // FILTER
    val filteredPatients = remember(patients, searchQuery) {
        if (searchQuery.isBlank()) patients
        else {
            val q = searchQuery.trim().lowercase()
            patients.filter { p ->
                p.name.lowercase().contains(q) ||
                        p.code.lowercase().contains(q) ||
                        p.nik.lowercase().contains(q) ||
                        p.phone.lowercase().contains(q) ||
                        p.guardianName.lowercase().contains(q) ||
                        p.gender.lowercase().contains(q)
            }
        }
    }

    // SORT
    val finalPatients = remember(filteredPatients, sortOption) {
        when (sortOption) {
            PatientSortOption.NAME_ASC -> filteredPatients.sortedBy { it.name.lowercase() }
            PatientSortOption.NAME_DESC -> filteredPatients.sortedByDescending { it.name.lowercase() }

            PatientSortOption.CODE_ASC -> filteredPatients.sortedBy { it.code.lowercase() }
            PatientSortOption.CODE_DESC -> filteredPatients.sortedByDescending { it.code.lowercase() }

            PatientSortOption.NEWEST -> filteredPatients.sortedByDescending { it.createdAt }
            PatientSortOption.OLDEST -> filteredPatients.sortedBy { it.createdAt }
        }
    }

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

            // =========================
            // HEADER: TITLE + SORT (POJOK KANAN)
            // =========================
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Data Pasien",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )

                Box {
                    IconButton(onClick = { sortExpanded = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                    }

                    DropdownMenu(
                        expanded = sortExpanded,
                        onDismissRequest = { sortExpanded = false }
                    ) {
                        PatientSortOption.entries.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt.label) },
                                onClick = {
                                    sortOption = opt
                                    sortExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // =========================
            // SEARCH (DIBAWAH JUDUL)
            // =========================
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it.replace("\n", "") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                label = { Text("Cari pasien...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Menampilkan ${finalPatients.size} dari ${patients.size} data",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (finalPatients.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (patients.isEmpty()) "Belum ada data pasien"
                        else "Data tidak ditemukan"
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(finalPatients) { patient ->
                        PatientCard(
                            patient = patient,
                            onCardClick = {
                                selectedPatient = patient
                                showPreviewDialog = true
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

    // PREVIEW (OK)
    if (showPreviewDialog && selectedPatient != null) {
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
                showPreviewDialog = false
                selectedPatient = null
            }
        )
    }

    // UPDATE CONFIRM
    if (showUpdateDialog && selectedPatient != null) {
        AlertDialog(
            onDismissRequest = {
                showUpdateDialog = false
                selectedPatient = null
            },
            title = { Text("Konfirmasi Update") },
            text = { Text("Lanjutkan untuk mengupdate data ${selectedPatient!!.name}?") },
            confirmButton = {
                Button(onClick = {
                    val p = selectedPatient!!
                    showUpdateDialog = false
                    selectedPatient = null
                    onUpdate(p)
                }) { Text("Submit") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showUpdateDialog = false
                    selectedPatient = null
                }) { Text("Cancel") }
            }
        )
    }

    // DELETE CONFIRM
    if (showDeleteDialog && selectedPatient != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                selectedPatient = null
            },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Yakin ingin menghapus ${selectedPatient!!.name}?") },
            confirmButton = {
                Button(onClick = {
                    val p = selectedPatient!!
                    showDeleteDialog = false
                    selectedPatient = null
                    onDelete(p) { _, _ -> }
                }) { Text("Hapus") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showDeleteDialog = false
                    selectedPatient = null
                }) { Text("Batal") }
            }
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
        onClick = onCardClick,
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