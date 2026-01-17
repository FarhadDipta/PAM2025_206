package com.example.spms.ui.screen.nurses

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
import com.example.spms.data.model.Nurse
import com.example.spms.ui.components.InfoDialog
import com.example.spms.ui.components.SuccessDialog

private enum class NurseSortOption(val label: String) {
    NAME_ASC("Nama (A-Z)"),
    NAME_DESC("Nama (Z-A)"),
    CODE_ASC("Kode (A-Z)"),
    CODE_DESC("Kode (Z-A)"),
    NEWEST("Terbaru"),
    OLDEST("Terlama")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NurseListScreen(
    nurses: List<Nurse>,
    onBack: () -> Unit,
    onCreate: () -> Unit,
    onUpdate: (Nurse) -> Unit,
    onDelete: (Nurse, (Boolean, String) -> Unit) -> Unit
) {
    var selectedNurse by remember { mutableStateOf<Nurse?>(null) }

    var showPreviewDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    // Search
    var searchQuery by remember { mutableStateOf("") }

    // Sort
    var sortExpanded by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf(NurseSortOption.NEWEST) }

    // FILTER
    val filteredNurses = remember(nurses, searchQuery) {
        if (searchQuery.isBlank()) nurses
        else {
            val q = searchQuery.trim().lowercase()
            nurses.filter { n ->
                n.name.lowercase().contains(q) ||
                        n.code.lowercase().contains(q) ||
                        n.nip.lowercase().contains(q) ||
                        n.phone.lowercase().contains(q) ||
                        n.email.lowercase().contains(q) ||
                        n.gender.lowercase().contains(q) ||
                        n.address.lowercase().contains(q)
            }
        }
    }

    // SORT
    val finalNurses = remember(filteredNurses, sortOption) {
        when (sortOption) {
            NurseSortOption.NAME_ASC -> filteredNurses.sortedBy { it.name.lowercase() }
            NurseSortOption.NAME_DESC -> filteredNurses.sortedByDescending { it.name.lowercase() }

            NurseSortOption.CODE_ASC -> filteredNurses.sortedBy { it.code.lowercase() }
            NurseSortOption.CODE_DESC -> filteredNurses.sortedByDescending { it.code.lowercase() }

            NurseSortOption.NEWEST -> filteredNurses.sortedByDescending { it.createdAt }
            NurseSortOption.OLDEST -> filteredNurses.sortedBy { it.createdAt }
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

            // =========================
            // HEADER: TITLE + SORT (POJOK KANAN)
            // =========================
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Data Perawat",
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
                        NurseSortOption.entries.forEach { opt ->
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
                label = { Text("Cari perawat...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Menampilkan ${finalNurses.size} dari ${nurses.size} data",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (finalNurses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (nurses.isEmpty()) "Belum ada data perawat"
                        else "Data tidak ditemukan"
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(finalNurses) { nurse ->
                        NurseCard(
                            nurse = nurse,
                            onCardClick = {
                                selectedNurse = nurse
                                showPreviewDialog = true
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

    // PREVIEW (OK)
    if (showPreviewDialog && selectedNurse != null) {
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
                showPreviewDialog = false
                selectedNurse = null
            }
        )
    }

    // UPDATE CONFIRM
    if (showUpdateDialog && selectedNurse != null) {
        AlertDialog(
            onDismissRequest = {
                showUpdateDialog = false
                selectedNurse = null
            },
            title = { Text("Konfirmasi Update") },
            text = { Text("Lanjutkan untuk mengupdate data ${selectedNurse!!.name}?") },
            confirmButton = {
                Button(onClick = {
                    val n = selectedNurse!!
                    showUpdateDialog = false
                    selectedNurse = null
                    onUpdate(n)
                }) { Text("Submit") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showUpdateDialog = false
                    selectedNurse = null
                }) { Text("Cancel") }
            }
        )
    }

    // DELETE CONFIRM
    if (showDeleteDialog && selectedNurse != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                selectedNurse = null
            },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Yakin ingin menghapus ${selectedNurse!!.name}?") },
            confirmButton = {
                Button(onClick = {
                    val n = selectedNurse!!
                    showDeleteDialog = false
                    selectedNurse = null

                    onDelete(n) { success, msg ->
                        if (success) {
                            successMessage = msg
                            showSuccessDialog = true
                        }
                    }
                }) { Text("Hapus") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showDeleteDialog = false
                    selectedNurse = null
                }) { Text("Batal") }
            }
        )
    }

    // SUCCESS DIALOG
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
        onClick = onCardClick,
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