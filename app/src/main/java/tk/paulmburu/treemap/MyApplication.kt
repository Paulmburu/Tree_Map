package tk.paulmburu.treemap

import android.app.Application
import androidx.preference.PreferenceManager
import tk.paulmburu.treemap.storage.SharedPreferencesStorage
import tk.paulmburu.treemap.user.UserManager
import tk.paulmburu.treemap.utils.ThemeHelper

class MyApplication : Application() {
    val userManager by lazy {
        UserManager(SharedPreferencesStorage(this))
    }


    override fun onCreate() {
        super.onCreate()
        initThemeSharedPrefs()
    }

    fun initThemeSharedPrefs(){
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)
        val themePref =
            sharedPreferences.getString("themePref", ThemeHelper.DEFAULT_MODE)
        ThemeHelper.applyTheme(themePref!!)
    }
}