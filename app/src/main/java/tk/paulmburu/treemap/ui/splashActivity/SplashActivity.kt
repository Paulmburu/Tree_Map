package tk.paulmburu.treemap.ui.splashActivity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import tk.paulmburu.treemap.MainActivity
import tk.paulmburu.treemap.R

class SplashActivity : AppCompatActivity() {

    private lateinit var mainActivityIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mainActivityIntent = Intent(this, MainActivity::class.java)

    }
    /**
     * Callback from SignInFragment when username, email and password has been entered
     */
    fun onDetailsEntered() {
        startActivity(mainActivityIntent)
        finish()
    }
}