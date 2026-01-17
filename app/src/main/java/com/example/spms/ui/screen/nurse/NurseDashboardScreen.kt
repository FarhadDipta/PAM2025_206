package com.example.spms.ui.screen.nurse

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NurseDashboardScreen(
    onGoPatients: () -> Unit,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    // ✅ anti spam click
    var logoutLocked by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    TextButton(
                        enabled = !logoutLocked,
                        onClick = { showLogoutDialog = true }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Logout")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (logoutLocked) "..." else "Logout")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Dashboard Perawat",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(26.dp))

            Button(
                onClick = onGoPatients,
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Data Pasien")
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi") },
            text = { Text("Apakah ingin logout?") },
            confirmButton = {
                Button(
                    enabled = !logoutLocked,
                    onClick = {
                        logoutLocked = true
                        showLogoutDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                OutlinedButton(
                    enabled = !logoutLocked,
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }

    // ✅ Logout dilakukan setelah delay kecil (biar Nav selesai stabil)
    LaunchedEffect(logoutLocked) {
        if (logoutLocked) {
            delay(300)
            onLogout()
            logoutLocked = false
        }
    }
}