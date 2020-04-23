package tk.paulmburu.treemap.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView
import tk.paulmburu.treemap.MapsActivity
import tk.paulmburu.treemap.R
import tk.paulmburu.treemap.databinding.FragmentMapsBinding

/**
 * A simple [Fragment] subclass.
 */
class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var fab : FloatingActionButton


    private val TAG = MapsFragment::class.java.simpleName
    private val REQUEST_LOCATION_PERMISSION = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentMapsBinding.inflate(inflater)
        binding.setLifecycleOwner(this)


        fab = binding.root.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view -> Toast.makeText(context,"$TAG", Toast.LENGTH_LONG).show()
            view.findNavController().navigate(MapsFragmentDirections.actionMapsFragmentToTreeFragment())
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

        // Add a marker in Voi and move the camera
        val treeLatLon = LatLng(-3.416413, 38.500554)
        val zoomLevel = 15f
        map.addMarker(MarkerOptions().position(treeLatLon).title("Marker in Voi").icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_tree_24)))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(treeLatLon, zoomLevel))

    }

    fun enableMyLocation(){
        if (ActivityCompat.checkSelfPermission(
                activity!!.parent,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity!!.parent,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(
                activity!!.parent,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )

            return
        }else{
            map.setMyLocationEnabled(true)
        }

    }


}