package tk.paulmburu.treemap.ui.tree


import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.GeoPoint
import tk.paulmburu.treemap.MyApplication
import tk.paulmburu.treemap.R
import tk.paulmburu.treemap.databinding.FragmentTreeBinding
import tk.paulmburu.treemap.models.Tree
import tk.paulmburu.treemap.ui.signin.SignInFragmentDirections
import tk.paulmburu.treemap.user.UserManager
import tk.paulmburu.treemap.utils.setOnSingleClickListener

/**
 * A simple [Fragment] subclass.
 */
class TreeFragment : Fragment() {

    private lateinit var treeName: TextInputEditText
    private lateinit var tree: Tree
    private lateinit var userManager: UserManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: FragmentTreeBinding

    private var lat:Double = 0.0
    private var long: Double = 0.0

    private var currentTreeCount: Int = 0
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var REQUEST_CHECK_SETTINGS = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTreeBinding.inflate(inflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        createLocationRequest()

        val application = requireNotNull(this.activity).application
        userManager = (application as MyApplication).userManager
        currentTreeCount =  Integer.valueOf(userManager.treesPlantedByUser)

        val treeViewModel = ViewModelProviders.of(this)
            .get(TreeViewModel::class.java)



        treeName = binding.root.findViewById<TextInputEditText>(R.id.tree_name_text_input_id)

        binding.root.findViewById<Button>(R.id.submit_new_tree_id).setOnSingleClickListener {
            // navigation call here
            it.visibility = View.INVISIBLE
            binding.root.findViewById<ProgressBar>(R.id.add_tree_progressbar).visibility = View.VISIBLE
            currentTreeCount = currentTreeCount + 1

            tree = Tree((currentTreeCount).toString(),treeName.text.toString(),"icons8_tree_planting_48.png","Buba","paulmburu53@gmail.com",
                GeoPoint(lat,long), System.currentTimeMillis(),"Tree by ${userManager.username}"
            )

            treeViewModel.saveTreeToFirebase(tree,userManager.username, userManager.userEmail,(currentTreeCount).toString())
            userManager.updateTreesPlantedCount((currentTreeCount).toString())
            findNavController().navigate(TreeFragmentDirections.actionTreeFragmentToNiceWorkFragment())

        }
//
//        binding.root.findViewById<Button>(R.id.submit_new_tree_id).setOnClickListener{
//            treeViewModel._navigateToNiceWorkFragment.observe(this, Observer {
//                if(null != it){
//                    this.findNavController().navigate(TreeFragmentDirections.actionTreeFragmentToNiceWorkFragment())
//                    userManager.updateTreesPlantedCount((currentTreeCount++).toString())
//                    treeViewModel.navigateToNiceWorkFragmentComplete()
//                }
//            })
//        }



        return binding.root
    }

    private fun getLatLocationGeopoint(binding: FragmentTreeBinding) {
        checkSelfLocationPermission()
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    lat = location.latitude
                    long = location.longitude
                    Log.e(this.toString(), "$location.latitude = $location.longitude")
                }
                binding.root.findViewById<Button>(R.id.get_area_location_loading_button_id)
                    .visibility = View.INVISIBLE
                binding.root.findViewById<Button>(R.id.area_location_loading_success_button_id)
                    .visibility = View.VISIBLE
            }
    }

    private fun checkSelfLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this.context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this.context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }
    fun createLocationRequest() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)

        val client: SettingsClient = LocationServices.getSettingsClient(this.activity!!)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            getLatLocationGeopoint(binding)

        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(this.activity,
                        REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }


    }


}
