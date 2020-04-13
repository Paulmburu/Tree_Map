package tk.paulmburu.treemap.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import tk.paulmburu.treemap.R
import tk.paulmburu.treemap.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,
            R.layout.activity_main
        )
        drawerLayout = binding.drawerLayout


        navController = findNavController(R.id.myNavHostFragment)

        appBarConfiguration = AppBarConfiguration(navController.graph,drawerLayout  )

        findViewById<NavigationView>(R.id.navView)
            .setupWithNavController(navController)


        bottomNavView = findViewById(R.id.bottom_navigation)
        NavigationUI.setupWithNavController(bottomNavView,navController)

//        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
//
//
//        setSupportActionBar(findViewById(R.id.my_toolbar))
//
//        findViewById<Toolbar>(R.id.my_toolbar)
//            .setupWithNavController(navController, appBarConfiguration)
    }
}
