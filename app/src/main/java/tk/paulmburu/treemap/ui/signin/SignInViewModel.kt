package tk.paulmburu.treemap.ui.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import tk.paulmburu.treemap.user.UserManager

class SignInViewModel(private val userManager: UserManager){
    private val _signinState = MutableLiveData<SigninViewState>()
    val signinState: LiveData<SigninViewState>
        get() = _signinState

    fun signin(username: String, email: String, password: String){
        if(userManager.loginUser(username,email,password)){
            _signinState.value = SigninSuccess
        }else{
            _signinState.value = SigninError
        }
    }

    fun unregister() {
        userManager.unregister()
    }

    fun getUsername(): String = userManager.username

    fun getUserEmail(): String = userManager.userEmail
}