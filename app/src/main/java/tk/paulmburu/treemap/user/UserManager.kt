package tk.paulmburu.treemap.user

import tk.paulmburu.treemap.storage.Storage


private const val REGISTERED_USER = "registered_user"
private const val REGISTERED_USER_EMAIL = "registered_user_email"
private const val PASSWORD_SUFFIX = "password"

class UserManager(private val storage: Storage) {
    /**
     *  UserDataRepository is specific to a logged in user. This determines if the user
     *  is logged in or not, when the user logs in, a new instance will be created.
     *  When the user logs out, this will be null.
     */
    var userDataRepository: UserDataRepository? = null

    val username: String
        get() = storage.getString(REGISTERED_USER)

    val userEmail: String
        get() = storage.getString(REGISTERED_USER_EMAIL)

    fun isUserLoggedIn() = userDataRepository != null

    fun isUserRegistered() = storage.getString(REGISTERED_USER).isNotEmpty()

    fun registerUser(username:String, userEmail: String, password: String){
        storage.setString(REGISTERED_USER,username)
        storage.setString(REGISTERED_USER_EMAIL,userEmail)
        storage.setString("$username$PASSWORD_SUFFIX",password)
        userJustLoggedIn()
    }

    fun loginUser(username: String,userEmail: String, password: String) : Boolean{
        val registeredUser = this.username
        val registeredUserEmail = this.userEmail

        if(registeredUser != username) return false
        if(registeredUserEmail != userEmail) return false

        val registeredPassword = storage.getString("$username$PASSWORD_SUFFIX")
        if(registeredPassword != password) return false

        userJustLoggedIn()
        return true
    }

    fun logout(){
        userDataRepository = null
    }

    fun unregister(){
        val username = storage.getString(REGISTERED_USER)
        storage.setString(REGISTERED_USER,"")
        storage.setString(REGISTERED_USER_EMAIL,"")
        storage.setString("$username$PASSWORD_SUFFIX","")
        logout()
    }

    private fun userJustLoggedIn() {
        userDataRepository = UserDataRepository(this)
    }
}