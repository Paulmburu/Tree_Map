package tk.paulmburu.treemap.ui.splashActivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tk.paulmburu.treemap.MainActivity
import tk.paulmburu.treemap.MyApplication
import tk.paulmburu.treemap.R
import tk.paulmburu.treemap.utils.AuthenticationState
import tk.paulmburu.treemap.utils.showSnackbar

class SplashActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SplashActivity"
        const val SIGN_IN_RESULT_CODE = 1001
        private val scope = CoroutineScope(Dispatchers.Main)
    }

    private lateinit var viewModel: SplashActivityViewModel

    private lateinit var mainActivityIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        viewModel =ViewModelProviders.of(this)[SplashActivityViewModel::class.java]
        mainActivityIntent = Intent(this, MainActivity::class.java)

        scope.launch {
            delay(1000)

            viewModel.authenticationState.observe(this@SplashActivity, Observer { authenticationState ->

                when(authenticationState){
                    AuthenticationState.AUTHENTICATED -> {
                        startActivity(mainActivityIntent)
                    }
                    AuthenticationState.UNAUTHENTICATED -> {
                        launchSignInFlow()
                    }
                }
            })
        }

    }

    private fun launchSignInFlow() {
        // Give users the option to sign in / register with their email or Google account.
        // If users choose to register with their email,
        // they will need to create a password as well.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()

            // This is where you can provide more ways for users to register and
            // sign in.
        )

        // Create and launch sign-in intent.
        // We listen to the response of this activity with the
        // SIGN_IN_REQUEST_CODE
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            SIGN_IN_RESULT_CODE

        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // User successfully signed in
                Log.i(TAG, "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!")
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // resp onse.getError().getErrorCode() and handle the error.
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }
}