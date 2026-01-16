package com.example.spms.ui.navigation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.spms.ui.components.SuccessDialog
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
    var showSuccessDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        // =========================
        // SPLASH
        // =========================
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // =========================
        // LOGIN
        // =========================
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

        // =========================
        // ADMIN DASHBOARD
        // =========================
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

        // =========================
        // NURSE DASHBOARD
        // =========================
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

        // ==========================================================
        // ========================= PERAWAT =========================
        // ==========================================================
        composable(Routes.NURSE_LIST) {
            val db = FirebaseFirestore.getInstance()
            val viewModel: NurseViewModel = remember { NurseViewModel(db, nurseRepo) }
            val nurses by viewModel.nurses.collectAsState()

            // Dialog state (biar tidak double)
            var selectedNurse by remember { mutableStateOf<Nurse?>(null) }
            var showUpdateDialog by remember { mutableStateOf(false) }
            var showDeleteDialog by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) { viewModel.startRealtime() }
            DisposableEffect(Unit) { onDispose { viewModel.stopRealtime() } }

            NurseListScreen(
                nurses = nurses,
                onBack = { navController.popBackStack() },
                onCreate = { navController.navigate(Routes.NURSE_CREATE) },

                // tombol update -> buka dialog konfirmasi update
                onUpdate = { nurse ->
                    selectedNurse = nurse
                    showUpdateDialog = true
                },

                // tombol delete -> buka dialog konfirmasi delete
                onDelete = { nurse ->
                    selectedNurse = nurse
                    showDeleteDialog = true
                }
            )

            // =========================
            // UPDATE CONFIRM DIALOG
            // =========================
            if (showUpdateDialog && selectedNurse != null) {
                AlertDialog(
                    onDismissRequest = { showUpdateDialog = false },
                    title = { Text("Konfirmasi Update") },
                    text = { Text("Lanjutkan untuk mengupdate data ${selectedNurse!!.name}?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                val code = selectedNurse!!.code
                                showUpdateDialog = false
                                selectedNurse = null
                                navController.navigate(Routes.nurseUpdateRoute(code))
                            }
                        ) { Text("Submit") }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = {
                                showUpdateDialog = false
                                selectedNurse = null
                            }
                        ) { Text("Cancel") }
                    }
                )
            }

            // =========================
            // DELETE CONFIRM DIALOG
            // =========================
            if (showDeleteDialog && selectedNurse != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Konfirmasi Hapus") },
                    text = { Text("Yakin ingin menghapus ${selectedNurse!!.name}?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                val code = selectedNurse!!.code
                                showDeleteDialog = false
                                selectedNurse = null

                                viewModel.deleteNurse(code) { ok, msg ->
                                    if (ok) {
                                        successMessage = "Data berhasil dihapus"
                                        showSuccessDialog = true
                                    }
                                }
                            }
                        ) { Text("Hapus") }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = {
                                showDeleteDialog = false
                                selectedNurse = null
                            }
                        ) { Text("Batal") }
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

        // ==========================================================
        // ========================== PASIEN =========================
        // ==========================================================
        composable(Routes.PATIENT_LIST) {
            val db = FirebaseFirestore.getInstance()
            val viewModel: PatientViewModel = remember { PatientViewModel(db, patientRepo) }
            val patients by viewModel.patients.collectAsState()

            var selectedPatient by remember { mutableStateOf<Patient?>(null) }
            var showUpdateDialog by remember { mutableStateOf(false) }
            var showDeleteDialog by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) { viewModel.startRealtime() }
            DisposableEffect(Unit) { onDispose { viewModel.stopRealtime() } }

            PatientListScreen(
                patients = patients,
                onBack = { navController.popBackStack() },
                onCreate = { navController.navigate(Routes.PATIENT_CREATE) },

                // tombol update -> dialog konfirmasi update
                onUpdate = { patient ->
                    selectedPatient = patient
                    showUpdateDialog = true
                },

                // tombol delete -> dialog konfirmasi delete
                onDelete = { patient ->
                    selectedPatient = patient
                    showDeleteDialog = true
                }
            )

            // =========================
            // UPDATE CONFIRM DIALOG
            // =========================
            if (showUpdateDialog && selectedPatient != null) {
                AlertDialog(
                    onDismissRequest = { showUpdateDialog = false },
                    title = { Text("Konfirmasi Update") },
                    text = { Text("Lanjutkan untuk mengupdate data ${selectedPatient!!.name}?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                val code = selectedPatient!!.code
                                showUpdateDialog = false
                                selectedPatient = null
                                navController.navigate(Routes.patientUpdateRoute(code))
                            }
                        ) { Text("Submit") }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = {
                                showUpdateDialog = false
                                selectedPatient = null
                            }
                        ) { Text("Cancel") }
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
                        Button(
                            onClick = {
                                val code = selectedPatient!!.code
                                showDeleteDialog = false
                                selectedPatient = null

                                viewModel.deletePatient(code) { ok, msg ->
                                    if (ok) {
                                        successMessage = "Data berhasil dihapus"
                                        showSuccessDialog = true
                                    }
                                }
                            }
                        ) { Text("Hapus") }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = {
                                showDeleteDialog = false
                                selectedPatient = null
                            }
                        ) { Text("Batal") }
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

    if (showSuccessDialog) {
        SuccessDialog(
            message = successMessage,
            onDismiss = { showSuccessDialog = false }
        )
    }
}