package tk.paulmburu.treemap.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import tk.paulmburu.treemap.user.UserManager

private const val MAX_LENGTH = 5

class SignUpViewModel() {


    private val _signupDetailsState = MutableLiveData<SignupDetailsState>()
    val signupDetailsState : LiveData<SignupDetailsState>
        get() = _signupDetailsState

    fun validateInput(username: String, email: String, password: String){
        when{
            username.length < MAX_LENGTH -> _signupDetailsState.value =
                SignupDetailsError("Username has to be longer than 4 characters")
            email.isEmpty() -> _signupDetailsState.value =
                SignupDetailsError("Email cannot be empty")
            password.length < MAX_LENGTH -> _signupDetailsState.value =
                SignupDetailsError("Password has to be longer than 4 characters")
            else -> _signupDetailsState.value = SignupDetailsSuccess
        }
    }


}