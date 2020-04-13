package tk.paulmburu.treemap.ui.maps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import tk.paulmburu.treemap.R

/**
 * A simple [Fragment] subclass.
 */
class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        view?.findViewById<FloatingActionButton>(R.id.add_new_tree_id)?.setOnClickListener( View.OnClickListener {
            Toast.makeText(context, " fab", Toast.LENGTH_LONG).show()
                        it.findNavController().navigate(MapsFragmentDirections.actionMapsFragmentToTreeFragment())
        })
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)

        return inflater.inflate(R.layout.fragment_maps, container, false)
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
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-3.0, 39.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Voi"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

//    fun newTree(view: View){
//        view.findNavController().navigate(MapsFragmentDirections.actionMapsFragmentToTreeFragment())
//    }

}