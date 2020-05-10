package tk.paulmburu.treemap

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    val host: NavHostFragment? by lazy {
        this.supportFragmentManager
            .findFragmentById(R.id.myNavHostFragment) as NavHostFragment?
    }

    private lateinit var bottomNavView: BottomNavigationView
    private val TAG = MapsActivity::class.java.simpleName
    private val REQUEST_LOCATION_PERMISSION = 1

//    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
//        when (menuItem.itemId) {
//            R.id.profile_bottom_nav_id -> {
//                Toast.makeText(this,"Profile",Toast.LENGTH_LONG).show()
////                    val fragment = ProfileFragment()
////                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
////                        .commit()
//                toolbar_main.visibility= View.GONE
//                val newFragment = SignInFragment()
//                val transaction = supportFragmentManager.beginTransaction()
//                transaction.replace(R.id.container, newFragment)
//                transaction.addToBackStack(null)
//                transaction.commit()
//
//
//                return@OnNavigationItemSelectedListener true
//            }
//            R.id.donate_bottom_nav_id -> {
////                    val fragment = ChapterFragment()
////                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
////                        .commit()
//                return@OnNavigationItemSelectedListener true
//            }
//            R.id.about_bottom_nav_id -> {
////                    val fragment = StoreFragment()
////                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
////                        .commit()
//                return@OnNavigationItemSelectedListener true
//            }
//
//            R.id.settings_bottom_nav_id-> {
////                    val fragment = StoreFragment()
////                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
////                        .commit()
//                return@OnNavigationItemSelectedListener true
//            }
//        }
//        false
//    }

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)



        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title="Search"
        setSupportActionBar(toolbar)

        bottomNavView = findViewById(R.id.bottom_navigation)
//        bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_HYBRID

        // Add a marker in Sydney and move the camera
        val treeLatLon = LatLng(-3.416413, 38.500554)
        val zoomLevel = 15f
        map.addMarker(MarkerOptions().position(treeLatLon).title("Marker in Voi"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(treeLatLon, zoomLevel))

        val overlaySize = 100f
        val androidOverlay = GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.icons8_oak_tree_100))
            .position(treeLatLon, overlaySize)

        map.addGroundOverlay(androidOverlay)
        setMapLongClick(map)
        enableMyLocation()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setMapLongClick(map:GoogleMap) {
        map.setOnMapLongClickListener {
            latLng ->
            // A Snippet is Additional text that's displayed below the title.
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )

            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))

        }
    }

    // By default, points of interest (POIs) appear on the map along with their corresponding icons.
    // POIs include parks, schools, government buildings, and more. When the map type is set to normal,
    // business POIs also appear on the map. Business POIs represent businesses such as shops, restaurants, and hotels.
    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker.showInfoWindow()
        }
    }

//    private fun setMapStyle(map: GoogleMap) {
//        try {
//            // Customize the styling of the base map using a JSON object defined
//            // in a raw resource file.
//            val success = map.setMapStyle(
//                MapStyleOptions.loadRawResourceStyle(
//                    this,
//                    R.map.maps_styles_retro
//                )
//            )
//
//            if (!success) {
//                Log.e(TAG, "Style parsing failed.")
//            }
//        } catch (e: Resources.NotFoundException) {
//            Log.e(TAG, "Can't find style. Error: ", e)
//        }
//    }

    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.setMyLocationEnabled(true)
        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }
}
