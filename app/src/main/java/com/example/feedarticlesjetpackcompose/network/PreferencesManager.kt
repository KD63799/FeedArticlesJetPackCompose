package com.example.feedarticlesjetpackcompose.network

import android.content.SharedPreferences
import com.example.feedarticlesjetpackcompose.utils.TOKEN
import com.example.feedarticlesjetpackcompose.utils.USER_ID
import javax.inject.Inject

class PreferencesManager @Inject constructor(private val prefs: SharedPreferences) {

    var currentUserId: Long
        get() = prefs.getLong(USER_ID, 0L)
        set(value) = prefs.edit().putLong(USER_ID, value).apply()

    var authToken: String?
        get() = prefs.getString(TOKEN, null)
        set(value) = prefs.edit().putString(TOKEN, value).apply()
}
