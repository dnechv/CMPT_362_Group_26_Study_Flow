package com.example.studyflow.loginPage


import android.content.Context
import android.content.SharedPreferences

//stores login info so no need to relogin

class SharedPreferencesLogin(context: Context) {
    private val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "UserPreferences"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_ID = "userId"
    }

    // Save login state (user ID and login status)
    fun setLoggedIn(isLoggedIn: Boolean, userId: String) {
        val editor = sharedPref.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.putString(KEY_USER_ID, userId)
        editor.apply()
    }

    // Check if the user is logged in
    fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Get the user ID
    fun getUserId(): String? {
        return sharedPref.getString(KEY_USER_ID, null)
    }

    // Log the user out (clear preferences)
    fun logout() {
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }
}