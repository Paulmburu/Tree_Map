package tk.paulmburu.treemap.ui.tree


import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
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
import tk.paulmburu.treemap.ui.signin.SignInFragmentDirections
import tk.paulmburu.treemap.user.UserManager
import tk.paulmburu.treemap.utils.setOnSingleClickListener
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class TreeFragment : Fragment() {

    private lateinit var treeName: TextInputEditText
    private lateinit var treeSpecie: TextInputEditText
    private lateinit var regionName: TextInputEditText
    private lateinit var tree: Tree
    private lateinit var userManager: UserManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: FragmentTreeBinding
    private lateinit var currentPhotoPath: String
    private lateinit var newTreeImageView: CircleImageView


    private var lat:Double = 0.0
    private var long: Double = 0.0

    private var currentTreeCount: Int = 0
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var REQUEST_CHECK_SETTINGS = 2
    val REQUEST_TAKE_PHOTO = 3
    val REQUEST_IMAGE_CAPTURE = 1


    private val TAG = "BUBA_PROFILE_FRAGMENT"


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

        newTreeImageView = binding.root.findViewById<CircleImageView>(R.id.new_tree_image_id)
        treeName = binding.root.findViewById<TextInputEditText>(R.id.tree_name_text_input_id)
        treeSpecie = binding.root.findViewById<TextInputEditText>(R.id.tree_species_text_input_id)
        regionName = binding.root.findViewById<TextInputEditText>(R.id.region_text_input_id)

        newTreeImageView.setOnClickListener {
            dispatchTakePicture_Intent()
        }

        binding.root.findViewById<Button>(R.id.submit_new_tree_id).setOnSingleClickListener {
            // navigation call here

            if(lat == 0.0 && long==0.0){
                locationDialog()
            }

            it.visibility = View.INVISIBLE
            binding.root.findViewById<ProgressBar>(R.id.add_tree_progressbar).visibility = View.VISIBLE
            currentTreeCount = currentTreeCount + 1
            regionName.text.toString()
            tree = Tree( System.currentTimeMillis(),(currentTreeCount).toString(),treeName.text.toString(),treeSpecie.text.toString(),regionName.text.toString(),"icons8_tree_planting_48.png","Buba","paulmburu53@gmail.com",
                GeoPoint(lat,long),"Tree by ${userManager.username}"
            )

            treeViewModel.saveTreeToFirebase(tree,userManager.username, userManager.userEmail,(currentTreeCount).toString(),regionName.text.toString())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            val imageBitmap = data!!.extras!!.get("data") as Bitmap
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


    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Log.d(TAG,"Error occurred while creating the File")
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this!!.context!!,
                        "tk.paulmburu.treemap.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }



    fun locationDialog(){
        // Initialize a new instance of
        val builder = AlertDialog.Builder(this!!.context!!)

        builder.setTitle("Location Adjustments")
        builder.setMessage("Please adjustment your with the location button on the maps section")

        builder.setPositiveButton("YES"){dialog, which ->
            // Do something when user press the positive button
//            Toast.makeText(context,"Ok, we change the app background.",Toast.LENGTH_SHORT).show()
            binding.root.findNavController().navigate(TreeFragmentDirections.actionTreeFragmentToMapsFragment())
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
