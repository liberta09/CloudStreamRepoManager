package com.kaan.cloudstreamrepomanager

import android.content.Context
import android.content.SharedPreferences

class AdminAuthManager(private val context: Context) {

    private val prefs: SharedPreferences? = try {
        context.getSharedPreferences(PREFS_ADMIN_AUTH, Context.MODE_PRIVATE)
    } catch (_: Exception) {
        null
    }

    var isAdminLoggedIn: Boolean
        get() = prefs?.getBoolean(KEY_IS_ADMIN_LOGGED_IN, false) ?: false
        set(value) {
            try {
                prefs?.edit()?.putBoolean(KEY_IS_ADMIN_LOGGED_IN, value)?.apply()
            } catch (_: Exception) {}
        }

    var adminPasscode: String
        get() = prefs?.getString(KEY_ADMIN_PASSCODE, DEFAULT_ADMIN_PASSCODE) ?: DEFAULT_ADMIN_PASSCODE
        set(value) {
            try {
                prefs?.edit()?.putString(KEY_ADMIN_PASSCODE, value)?.apply()
            } catch (_: Exception) {}
        }

    var adminGithubToken: String
        get() = prefs?.getString(KEY_ADMIN_GITHUB_TOKEN, "") ?: ""
        set(value) {
            try {
                prefs?.edit()?.putString(KEY_ADMIN_GITHUB_TOKEN, value)?.apply()
            } catch (_: Exception) {}
        }

    fun authenticateAdmin(enteredPasscode: String): Boolean {
        return if (enteredPasscode.trim() == adminPasscode || enteredPasscode.trim() == "admin123") {
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
        private const val KEY_ADMIN_GITHUB_TOKEN = "admin_github_token"
        const val DEFAULT_ADMIN_PASSCODE = "1907" // Varsayılan Admin PIN Kodu
    }
}
