package tk.paulmburu.treemap.ui.splashActivity


import androidx.lifecycle.*
import tk.paulmburu.treemap.repository.FirestoreRepository
import tk.paulmburu.treemap.utils.AuthenticationState
import tk.paulmburu.treemap.utils.FirebaseUserLiveData


class SplashActivityViewModel : ViewModel() {
    var firebaseRepository = FirestoreRepository()
    private val TAG = "SplashActivityViewModel"

    val authenticationState  = Transformations.map(FirebaseUserLiveData()){ user ->
        if (user != null) {
            registerUser(user.displayName!!, user.email!!)
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

    fun registerUser(username: String, userEmail: String) {
        firebaseRepository.createNewArborist(username!!,userEmail!!)
    }


}



