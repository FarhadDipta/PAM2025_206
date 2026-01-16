package com.example.spms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.spms.data.repository.AuthRepository
import com.example.spms.data.repository.CounterRepository
import com.example.spms.data.repository.NurseRepository
import com.example.spms.data.repository.PatientRepository
import com.example.spms.ui.components.AppBackground
import com.example.spms.ui.navigation.AppNavGraph
import com.example.spms.ui.theme.SPMSTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        val db = FirebaseFirestore.getInstance()
        val counterRepo = CounterRepository(db)
        val authRepo = AuthRepository(db)
        val nurseRepo = NurseRepository(db, counterRepo)
        val patientRepo = PatientRepository(db, counterRepo)

        setContent {
            SPMSTheme {
                AppBackground {
                    val navController = rememberNavController()
                    AppNavGraph(
                        navController = navController,
                        authRepo = authRepo,
                        nurseRepo = nurseRepo,
                        patientRepo = patientRepo
                    )
                }
            }
        }
    }
}