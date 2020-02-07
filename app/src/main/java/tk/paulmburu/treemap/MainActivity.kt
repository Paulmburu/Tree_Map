package tk.paulmburu.treemap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import tk.paulmburu.treemap.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout


        val navController = findNavController(R.id.myNavHostFragment)

        appBarConfiguration = AppBarConfiguration(navController.graph,drawerLayout  )

        findViewById<NavigationView>(R.id.navView)
            .setupWithNavController(navController)

//        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
//
//
//        setSupportActionBar(findViewById(R.id.my_toolbar))
//
//        findViewById<Toolbar>(R.id.my_toolbar)
//            .setupWithNavController(navController, appBarConfiguration)
    }
}
