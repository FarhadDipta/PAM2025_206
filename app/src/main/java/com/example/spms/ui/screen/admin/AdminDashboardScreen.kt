package com.example.spms.ui.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onGoNurses: () -> Unit,
    onGoPatients: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Topbar custom: logout di kiri atas
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    TextButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Logout"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Logout")
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

            // kasih jarak biar judul turun ke bawah rapih
            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Dashboard Admin",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(26.dp))

            // tombol di tengah seperti TextField login (tidak full layar)
            val buttonWidth = 0.95f

            Button(
                onClick = onGoNurses,
                modifier = Modifier
                    .fillMaxWidth(buttonWidth)
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = "Data Perawat",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onGoPatients,
                modifier = Modifier
                    .fillMaxWidth(buttonWidth)
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = "Data Pasien",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}