package com.example.spms.ui.navigation

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val NURSE_DASHBOARD = "nurse_dashboard"

    const val NURSE_LIST = "nurse_list"
    const val NURSE_CREATE = "nurse_create"
    const val NURSE_UPDATE = "nurse_update/{code}"
    fun nurseUpdateRoute(code: String) = "nurse_update/$code"

    const val PATIENT_LIST = "patient_list"
    const val PATIENT_CREATE = "patient_create"
    const val PATIENT_UPDATE = "patient_update/{code}"

    fun patientUpdateRoute(code: String) = "patient_update/$code"
}