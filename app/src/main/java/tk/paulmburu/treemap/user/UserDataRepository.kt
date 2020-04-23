package tk.paulmburu.treemap.user

class UserDataRepository(private val userManager: UserManager) {

    val username: String
        get() = userManager.username

    val userEmail: String
        get() = userManager.userEmail
}