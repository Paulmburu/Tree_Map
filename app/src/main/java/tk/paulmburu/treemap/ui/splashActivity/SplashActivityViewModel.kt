package tk.paulmburu.treemap.ui.splashActivity


import tk.paulmburu.treemap.repository.FirestoreRepository
import tk.paulmburu.treemap.user.UserManager

class SplashActivityViewModel(private val userManager: UserManager) {
    var firebaseRepository = FirestoreRepository()
    private val TAG = "BUBA_HOME"


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

        firebaseRepository.createNewArborist(username!!,userEmail!!)
        userManager.registerUser(username!!, userEmail!!, password!!)
    }

}

