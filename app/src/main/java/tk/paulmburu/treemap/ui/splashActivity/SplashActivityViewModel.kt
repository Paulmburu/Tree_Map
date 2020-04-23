package tk.paulmburu.treemap.ui.splashActivity

import tk.paulmburu.treemap.user.UserManager

class SplashActivityViewModel(private val userManager: UserManager) {
    private var username: String? = null
    private var userEmail: String? = null
    private var password: String? = null


    fun updateUserData(username: String, userEmail: String,password: String) {
        this.username = username
        this.userEmail = userEmail
        this.password = password
        registerUser()
    }

    fun registerUser() {
        assert(username != null)
        assert(userEmail != null)
        assert(password != null)

        userManager.registerUser(username!!, userEmail!!, password!!)
    }
}