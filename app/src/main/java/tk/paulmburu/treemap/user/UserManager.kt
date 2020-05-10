package tk.paulmburu.treemap.user

import tk.paulmburu.treemap.storage.Storage


private const val REGISTERED_USER = "registered_user"
private const val REGISTERED_USER_EMAIL = "registered_user_email"
private const val PASSWORD_SUFFIX = "password"
private const val TREES_PLANTED_BY_USER = "trees_planted"
private const val CURRENT_PROFILE_IMAGE = "profile_image"

class UserManager(private val storage: Storage) {
    /**
     *  UserDataRepository is specific to a logged in user. This determines if the user
     *  is logged in or not, when the user logs in, a new instance will be created.
     *  When the user logs out, this will be null.
     */
    var userDataRepository: UserDataRepository? = null

    val username: String
        get() = storage.getString(REGISTERED_USER)

    val password: String
        get() = storage.getString("$username$PASSWORD_SUFFIX")

    val userEmail: String
        get() = storage.getString(REGISTERED_USER_EMAIL)

    val treesPlantedByUser: String
        get() = storage.getString(TREES_PLANTED_BY_USER)

    val currentProfileImageUri: String
        get() = storage.getString(CURRENT_PROFILE_IMAGE)

    fun isUserLoggedIn() = userDataRepository != null

    fun isUserRegistered() = storage.getString(REGISTERED_USER).isNotEmpty()

    fun changePassword(password: String){
        storage.setString("$username$PASSWORD_SUFFIX",password)
    }

    fun setCurrentProfileImage(currentProfileImageUri: String){
        storage.setString("$CURRENT_PROFILE_IMAGE",currentProfileImageUri)
    }

    fun registerUser(username:String, userEmail: String, password: String){
        storage.setString(REGISTERED_USER,username)
        storage.setString(REGISTERED_USER_EMAIL,userEmail)
        storage.setString("$username$PASSWORD_SUFFIX",password)
        storage.setString(TREES_PLANTED_BY_USER,"0")
        userJustLoggedIn()
    }

    fun updateTreesPlantedCount(updatedCount: String){
        storage.setString(TREES_PLANTED_BY_USER,updatedCount)
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
        storage.setString("$CURRENT_PROFILE_IMAGE","")
        logout()
    }

    private fun userJustLoggedIn() {
        userDataRepository = UserDataRepository(this)
    }
}