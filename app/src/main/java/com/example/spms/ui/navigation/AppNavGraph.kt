package com.example.spms.ui.navigation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.spms.data.model.Nurse
import com.example.spms.data.model.Patient
import com.example.spms.data.repository.AuthRepository
import com.example.spms.data.repository.NurseRepository
import com.example.spms.data.repository.PatientRepository
import com.example.spms.ui.screen.admin.AdminDashboardScreen
import com.example.spms.ui.screen.login.LoginScreen
import com.example.spms.ui.screen.nurse.NurseDashboardScreen
import com.example.spms.ui.screen.nurses.NurseFormScreen
import com.example.spms.ui.screen.nurses.NurseListScreen
import com.example.spms.ui.screen.nurses.NurseUpdateScreen
import com.example.spms.ui.screen.nurses.NurseViewModel
import com.example.spms.ui.screen.patients.PatientFormScreen
import com.example.spms.ui.screen.patients.PatientListScreen
import com.example.spms.ui.screen.patients.PatientUpdateScreen
import com.example.spms.ui.screen.patients.PatientViewModel
import com.example.spms.ui.screen.splash.SplashScreen
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    authRepo: AuthRepository,
    nurseRepo: NurseRepository,
    patientRepo: PatientRepository
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                authRepo = authRepo,
                onLoginSuccess = { user ->
                    if (user.role == "ADMIN") {
                        navController.navigate(Routes.ADMIN_DASHBOARD) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.NURSE_DASHBOARD) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.ADMIN_DASHBOARD) {
            AdminDashboardScreen(
                onGoNurses = { navController.navigate(Routes.NURSE_LIST) },
                onGoPatients = { navController.navigate(Routes.PATIENT_LIST) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.NURSE_DASHBOARD) {
            NurseDashboardScreen(
                onGoPatients = { navController.navigate(Routes.PATIENT_LIST) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // CRUD PERAWAT (List + Create + Update)
        composable(Routes.NURSE_LIST) {
            val db = FirebaseFirestore.getInstance()
            val viewModel: NurseViewModel = remember { NurseViewModel(db, nurseRepo) }
            val nurses by viewModel.nurses.collectAsState()

            var showUpdateDialog by remember { mutableStateOf(false) }
            var showDeleteDialog by remember { mutableStateOf(false) }
            var selectedNurse by remember { mutableStateOf<Nurse?>(null) }

            LaunchedEffect(Unit) { viewModel.startRealtime() }
            DisposableEffect(Unit) { onDispose { viewModel.stopRealtime() } }

            NurseListScreen(
                nurses = nurses,
                onBack = { navController.popBackStack() },
                onCreate = { navController.navigate(Routes.NURSE_CREATE) },
                onUpdate = { nurse ->
                    selectedNurse = nurse
                    showUpdateDialog = true
                },
                onDelete = { nurse ->
                    selectedNurse = nurse
                    showDeleteDialog = true
                }
            )

            if (showUpdateDialog) {
                AlertDialog(
                    onDismissRequest = { showUpdateDialog = false },
                    title = { Text("Konfirmasi Update") },
                    text = { Text("Lanjutkan untuk mengupdate data ${selectedNurse?.name}?") },
                    confirmButton = {
                        Button(onClick = {
                            showUpdateDialog = false
                            selectedNurse?.let { navController.navigate(Routes.nurseUpdateRoute(it.code)) }
                        }) { Text("Update") }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showUpdateDialog = false }) { Text("Batal") }
                    }
                )
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Konfirmasi Hapus") },
                    text = { Text("Yakin ingin menghapus ${selectedNurse?.name}?") },
                    confirmButton = {
                        Button(onClick = {
                            showDeleteDialog = false
                            selectedNurse?.let { viewModel.deleteNurse(it.code) { _, _ -> } }
                        }) { Text("Hapus") }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
                    }
                )
            }
        }

        composable(Routes.NURSE_CREATE) {
            NurseFormScreen(
                nurseRepo = nurseRepo,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.NURSE_UPDATE,
            arguments = listOf(navArgument("code") { type = NavType.StringType })
        ) { backStackEntry ->
            val code = backStackEntry.arguments?.getString("code") ?: ""
            NurseUpdateScreen(
                nurseCode = code,
                nurseRepo = nurseRepo,
                onBack = { navController.popBackStack() }
            )
        }

        // CRUD PASIEN (List + Create + Update)
        composable(Routes.PATIENT_LIST) {
            val db = FirebaseFirestore.getInstance()
            val viewModel: PatientViewModel = remember { PatientViewModel(db, patientRepo) }
            val patients by viewModel.patients.collectAsState()

            var showUpdateDialog by remember { mutableStateOf(false) }
            var showDeleteDialog by remember { mutableStateOf(false) }
            var selectedPatient by remember { mutableStateOf<Patient?>(null) }

            LaunchedEffect(Unit) { viewModel.startRealtime() }
            DisposableEffect(Unit) { onDispose { viewModel.stopRealtime() } }

            PatientListScreen(
                patients = patients,
                onBack = { navController.popBackStack() },
                onCreate = { navController.navigate(Routes.PATIENT_CREATE) },
                onUpdate = { patient ->
                    selectedPatient = patient
                    showUpdateDialog = true
                },
                onDelete = { patient ->
                    selectedPatient = patient
                    showDeleteDialog = true
                }
            )

            if (showUpdateDialog) {
                AlertDialog(
                    onDismissRequest = { showUpdateDialog = false },
                    title = { Text("Konfirmasi Update") },
                    text = { Text("Lanjutkan untuk mengupdate data ${selectedPatient?.name}?") },
                    confirmButton = {
                        Button(onClick = {
                            showUpdateDialog = false
                            selectedPatient?.let { navController.navigate(Routes.patientUpdateRoute(it.code)) }
                        }) { Text("Update") }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showUpdateDialog = false }) { Text("Batal") }
                    }
                )
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Konfirmasi Hapus") },
                    text = { Text("Yakin ingin menghapus ${selectedPatient?.name}?") },
                    confirmButton = {
                        Button(onClick = {
                            showDeleteDialog = false
                            selectedPatient?.let { viewModel.deletePatient(it.code) { _, _ -> } }
                        }) { Text("Hapus") }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
                    }
                )
            }
        }

        composable(Routes.PATIENT_CREATE) {
            PatientFormScreen(
                patientRepo = patientRepo,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.PATIENT_UPDATE,
            arguments = listOf(navArgument("code") { type = NavType.StringType })
        ) { backStackEntry ->
            val code = backStackEntry.arguments?.getString("code") ?: ""
            PatientUpdateScreen(
                patientCode = code,
                patientRepo = patientRepo,
                onBack = { navController.popBackStack() }
            )
        }
    }
}