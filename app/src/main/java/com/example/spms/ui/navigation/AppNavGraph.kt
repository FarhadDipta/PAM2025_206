package com.example.spms.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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

        // =========================
        // SPLASH
        // =========================
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                        launchSingleTop = true
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
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate(Routes.NURSE_DASHBOARD) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                            launchSingleTop = true
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
                    // ✅ FIX logout: clear semua stack biar tidak blank hitam
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
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
                    // ✅ FIX logout: clear semua stack biar tidak blank hitam
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // =========================
        // NURSE LIST
        // =========================
        composable(Routes.NURSE_LIST) {
            val db = FirebaseFirestore.getInstance()
            val viewModel: NurseViewModel = remember { NurseViewModel(db, nurseRepo) }
            val nurses by viewModel.nurses.collectAsState()

            LaunchedEffect(Unit) { viewModel.startRealtime() }
            DisposableEffect(Unit) { onDispose { viewModel.stopRealtime() } }

            NurseListScreen(
                nurses = nurses,
                onBack = { navController.popBackStack() },
                onCreate = { navController.navigate(Routes.NURSE_CREATE) },
                onUpdate = { nurse ->
                    navController.navigate(Routes.nurseUpdateRoute(nurse.code))
                },
                onDelete = { nurse, onDone ->
                    viewModel.deleteNurse(nurse.code) { success, msg ->
                        onDone(success, msg)
                    }
                }
            )
        }

        // =========================
        // NURSE CREATE
        // =========================
        composable(Routes.NURSE_CREATE) {
            NurseFormScreen(
                nurseRepo = nurseRepo,
                onBack = { navController.popBackStack() }
            )
        }

        // =========================
        // NURSE UPDATE
        // =========================
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

        // =========================
        // PATIENT LIST
        // =========================
        composable(Routes.PATIENT_LIST) {
            val db = FirebaseFirestore.getInstance()
            val viewModel: PatientViewModel = remember { PatientViewModel(db, patientRepo) }
            val patients by viewModel.patients.collectAsState()

            LaunchedEffect(Unit) { viewModel.startRealtime() }
            DisposableEffect(Unit) { onDispose { viewModel.stopRealtime() } }

            PatientListScreen(
                patients = patients,
                onBack = { navController.popBackStack() },
                onCreate = { navController.navigate(Routes.PATIENT_CREATE) },
                onUpdate = { patient ->
                    navController.navigate(Routes.patientUpdateRoute(patient.code))
                },
                onDelete = { patient, onDone ->
                    viewModel.deletePatient(patient.code) { success, msg ->
                        onDone(success, msg)
                    }
                }
            )
        }

        // =========================
        // PATIENT CREATE
        // =========================
        composable(Routes.PATIENT_CREATE) {
            PatientFormScreen(
                patientRepo = patientRepo,
                onBack = { navController.popBackStack() }
            )
        }

        // =========================
        // PATIENT UPDATE
        // =========================
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