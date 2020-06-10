package tk.paulmburu.treemap.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.QueryDocumentSnapshot
import tk.paulmburu.treemap.MyApplication
import tk.paulmburu.treemap.databinding.FragmentMapsBinding
import tk.paulmburu.treemap.R
import tk.paulmburu.treemap.models.Tree
import tk.paulmburu.treemap.repository.FirestoreRepository
import tk.paulmburu.treemap.user.UserManager
import java.lang.Exception


/**
 * A simple [Fragment] subclass.
 */
class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var fab: FloatingActionButton
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var firebaseRepository: FirestoreRepository
    private lateinit var userManager: UserManager


    private val TAG = MapsFragment::class.java.simpleName
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentMapsBinding.inflate(inflater)
        binding.setLifecycleOwner(this)
        val application = requireNotNull(this.activity).application
        userManager = (application as MyApplication).userManager
//        mapsViewModel = MapsViewModel(application)
        mapsViewModel = ViewModelProviders.of(this)
            .get(MapsViewModel::class.java)

        firebaseRepository = FirestoreRepository()

        fab = binding.root.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view ->
            view.findNavController()
                .navigate(MapsFragmentDirections.actionMapsFragmentToTreeFragment())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapView = view.findViewById(R.id.map) as MapView
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this);//when you already implement OnMapReadyCallback in your fragment

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

        // Add a marker in Voi and move the camera = ""
//        val treeLatLon = LatLng(-3.416413, 38.500554)
        val zoomLevel = 15f


//        map.addMarker(
//            MarkerOptions().position(treeLatLon).title("Marker in Voi").icon(
//                BitmapDescriptorFactory.fromResource(R.drawable.icons8_tree_24)
//            )
//        )
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(treeLatLon, zoomLevel))

        firebaseRepository.getAllGlobalTrees().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document: QueryDocumentSnapshot in task.result!!) {
                    val tree = document.toObject(Tree::class.java)
                    map.addMarker(
                        MarkerOptions().position(LatLng(tree.tree_geopoint.latitude,tree.tree_geopoint.longitude))
                            .title(tree.treeDescription)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_tree_24)
                    ))

                }
            }

        }

        enableMyLocation()
    }

    private fun fetchData() { // JSON response
//        val treesList: List<Tree>? = mapsViewModel._trees.value

        try {
            // Clear old markers
            map.clear()
            // Looping through all info and show on map
            for (i in 0 until mapsViewModel._trees.value!!.size) {
                Toast.makeText(activity, "Tree: " + mapsViewModel._trees.value!![i], Toast.LENGTH_LONG).show()

                // Add marker
                addMarkerToMap(mapsViewModel._trees.value!![i].tree_geopoint.latitude.toString()
                    , mapsViewModel._trees.value!![i].tree_geopoint.longitude.toString()
                    , mapsViewModel._trees.value!![i].name
                    , mapsViewModel._trees.value!![i].treeDescription)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(activity, "Error: " + e.message, Toast.LENGTH_LONG).show()
        }
    }
    fun addMarkerToMap(
        latitude: String,
        longitude: String,
        title: String?,
        description: String?
    ) {
        val lat = latitude.toDouble()
        val lng = longitude.toDouble()
        // create marker
        val marker =
            MarkerOptions().position(LatLng(lat, lng))
                .title(title)
                .snippet(description)
        // Marker icon
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_tree_24))
        // Add marker to map
        map.addMarker(marker)
    }

    fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
                map.setMyLocationEnabled(true);

        } else {
            // Permission to access the location is missing. Show rationale and request permission
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            );
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            } else { // Permission was denied. Display an error message
// ...
            }
        }

    }
}