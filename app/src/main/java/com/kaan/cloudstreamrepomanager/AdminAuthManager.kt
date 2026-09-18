package com.kaan.cloudstreamrepomanager

import android.content.Context
import android.content.SharedPreferences

class AdminAuthManager(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_ADMIN_AUTH, Context.MODE_PRIVATE)

    var isAdminLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_ADMIN_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_ADMIN_LOGGED_IN, value).apply()

    var adminPasscode: String
        get() = prefs.getString(KEY_ADMIN_PASSCODE, DEFAULT_ADMIN_PASSCODE) ?: DEFAULT_ADMIN_PASSCODE
        set(value) = prefs.edit().putString(KEY_ADMIN_PASSCODE, value).apply()

    fun authenticateAdmin(enteredPasscode: String): Boolean {
        return if (enteredPasscode.trim() == adminPasscode) {
            isAdminLoggedIn = true
            true
        } else {
            false
        }
    }

    fun logoutAdmin() {
        isAdminLoggedIn = false
    }

    companion object {
        private const val PREFS_ADMIN_AUTH = "admin_auth_prefs"
        private const val KEY_IS_ADMIN_LOGGED_IN = "is_admin_logged_in"
        private const val KEY_ADMIN_PASSCODE = "admin_passcode"
        const val DEFAULT_ADMIN_PASSCODE = "1907" // Varsayılan Admin PIN Kodu
    }
}
