package app.musiko

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import app.musiko.helpers.ThemeHelper

val goPreferences: MusikoPreferences by lazy {
    MusikoApp.prefs
}

class MusikoApp : Application() {

    companion object {
        lateinit var prefs: MusikoPreferences
    }

    override fun onCreate() {
        super.onCreate()
        prefs = MusikoPreferences(applicationContext)
        AppCompatDelegate.setDefaultNightMode(ThemeHelper.getDefaultNightMode(applicationContext))
    }
}
