package tk.paulmburu.treemap.ui.tree


import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.GeoPoint
import de.hdodenhof.circleimageview.CircleImageView
import tk.paulmburu.treemap.MyApplication
import tk.paulmburu.treemap.R
import tk.paulmburu.treemap.databinding.FragmentTreeBinding
import tk.paulmburu.treemap.models.Tree
import tk.paulmburu.treemap.user.UserManager
import tk.paulmburu.treemap.utils.UserInfo
import tk.paulmburu.treemap.utils.setOnSingleClickListener

/**
 * A simple [Fragment] subclass.
 */
class TreeFragment : Fragment() {

    private lateinit var treeName: TextInputEditText
    private lateinit var treeSpecie: TextInputEditText
    private lateinit var regionName: TextInputEditText
    private lateinit var submitButton: Button
    private lateinit var userManager: UserManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: FragmentTreeBinding
    private lateinit var newTreeImageView: CircleImageView
    private lateinit var treeBitmap: Bitmap


    private var lat:Double = 0.0
    private var long: Double = 0.0

    private var currentTreeCount: Int = 0
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var REQUEST_CHECK_SETTINGS = 2
    val REQUEST_IMAGE_CAPTURE = 1


    private val TAG = "BUBA_PROFILE_FRAGMENT"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTreeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        createLocationRequest()

        val application = requireNotNull(this.activity).application
        userManager = (application as MyApplication).userManager
//        currentTreeCount =  Integer.valueOf(userManager.treesPlantedByUser)?: 0

        val treeViewModel = ViewModelProviders.of(this)
            .get(TreeViewModel::class.java)

        newTreeImageView = view.findViewById<CircleImageView>(R.id.new_tree_image_id)
        treeName = view.findViewById<TextInputEditText>(R.id.tree_name_text_input_id)
        treeSpecie = view.findViewById<TextInputEditText>(R.id.tree_species_text_input_id)
        regionName = view.findViewById<TextInputEditText>(R.id.region_text_input_id)
        submitButton = view.findViewById<Button>(R.id.submit_new_tree_id)

        newTreeImageView.setOnClickListener {
            dispatchTakePicture_Intent()
        }

        submitButton.setOnSingleClickListener {
            // navigation call here

            if(lat == 0.0 && long==0.0){
                locationDialog()
                return@setOnSingleClickListener
            }

            it.visibility = View.INVISIBLE
            view.findViewById<ProgressBar>(R.id.add_tree_progressbar).visibility = View.VISIBLE

            currentTreeCount = currentTreeCount + 1
            regionName.text.toString()


            val tree = Tree(
                System.currentTimeMillis(),
                (currentTreeCount).toString(),
                treeName.text.toString(),
                treeSpecie.text.toString(),
                regionName.text.toString(),
                treeBitmap.toString(),
                UserInfo.auth_username!!,
                UserInfo.auth_email!!,
                GeoPoint(lat,long),
                "Tree by ${UserInfo.auth_username!!}"
            )

            treeViewModel.saveTreeToFirebase(
                tree,
                UserInfo.auth_username!!,
                UserInfo.auth_email!!,
                (currentTreeCount).toString(),
                regionName.text.toString(),
                treeBitmap)
            userManager.updateTreesPlantedCount((currentTreeCount).toString())

            findNavController().navigate(
                TreeFragmentDirections.actionTreeFragmentToNiceWorkFragment()
            )

        }

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
                    .visibility = View.INVISIBLE
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

        task.addOnSuccessListener { _ ->
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data!!.extras!!.get("data") as Bitmap
            treeBitmap = imageBitmap
            newTreeImageView.setImageBitmap(imageBitmap)
        }
    }

    private fun dispatchTakePicture_Intent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    fun locationDialog(){
        // Initialize a new instance of
        val builder = AlertDialog.Builder(this.context!!)

        builder.setTitle("Location Adjustments")
        builder.setMessage("Please adjustment your with the location button on the maps section")

        builder.setPositiveButton("YES"){_, _ ->
            // Do something when user press the positive button
//            Toast.makeText(context,"Ok, we change the app background.",Toast.LENGTH_SHORT).show()
            binding.root.findNavController().navigate(
                TreeFragmentDirections.actionTreeFragmentToMapsFragment()
            )
        }


        // Display a negative button on alert dialog
//        builder.setNegativeButton("No"){dialog,which ->
//            Toast.makeText(context,"You are not agree.",Toast.LENGTH_SHORT).show()
//        }


        // Display a neutral button on alert dialog
//        builder.setNeutralButton("Cancel"){_,_ ->
//            Toast.makeText(applicationContext,"You cancelled the dialog.",Toast.LENGTH_SHORT).show()
//        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

}
