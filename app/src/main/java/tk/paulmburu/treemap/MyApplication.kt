package tk.paulmburu.treemap

import android.app.Application
import tk.paulmburu.treemap.storage.SharedPreferencesStorage
import tk.paulmburu.treemap.user.UserManager

class MyApplication : Application() {
    val userManager by lazy {
        UserManager(SharedPreferencesStorage(this))
    }
}