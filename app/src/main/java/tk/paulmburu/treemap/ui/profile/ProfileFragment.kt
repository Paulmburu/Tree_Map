package tk.paulmburu.treemap.ui.profile


import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView
import tk.paulmburu.treemap.MyApplication
import tk.paulmburu.treemap.R
import tk.paulmburu.treemap.databinding.FragmentProfileBinding
import tk.paulmburu.treemap.user.UserManager
import tk.paulmburu.treemap.utils.UserInfo
import tk.paulmburu.treemap.utils.showSnackbar
import tk.paulmburu.treemap.utils.showToast


/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    private val TAG = "PROFILE_FRAGMENT"
    val READ_STORAGE_PERMISSION_REQUEST_CODE = 2

    private lateinit var binding: FragmentProfileBinding
    private lateinit var userManager: UserManager
    private lateinit var profileImageView: CircleImageView
    private lateinit var viewModel: ProfileViewModel

    private val storage = Firebase.storage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application
        userManager = (application as MyApplication).userManager
        viewModel = ViewModelProviders.of(this, ProfileViewModel.Factory(userManager))
            .get(ProfileViewModel::class.java)

        viewModel.status.observe(viewLifecycleOwner, Observer {
            when(it){
                is LoadingProfileImageDone -> {
                    binding.root.findViewById<ImageView>(R.id.loading_profile_image).visibility = View.INVISIBLE
                    showSnackbar(binding.root.findViewById(R.id.profile_constraint_layout),"Image uploaded successfully")
                }

                is LoadingProfileImage -> {
                    binding.root.findViewById<ImageView>(R.id.loading_profile_image).visibility = View.VISIBLE
                    showSnackbar(binding.root.findViewById(R.id.profile_constraint_layout),"Upload is at ${it.percentage}%")
                }

            }
        })

        profileImageView = binding.root.findViewById<CircleImageView>(R.id.user_image_view_id)
        profileImageView.setOnClickListener {
                selectImage(this.context!!)
        }

        if (userManager.currentProfileImageUri.isNotEmpty()) {
            checkSelfReadStoragePermission()
                profileImageView.setImageURI(Uri.parse(userManager.currentProfileImageUri))


        }



        binding.root.findViewById<TextView>(R.id.user_name_id).apply {
            setText(UserInfo.auth_username)
        }

        userManager.treesPlantedByUser.apply {
            binding.root.findViewById<TextView>(R.id.user_planted_trees_id).setText(this)
            binding.root.findViewById<TextView>(R.id.total_trees_planted_tv_id).setText(this)
        }


        return binding.root
    }

    private fun selectTheImage() {
        var getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.setType("image/*")

        var pickIntent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        getIntent.setType("image/*")

        val chooserIntent = Intent.createChooser(getIntent, "Select Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

        startActivityForResult(chooserIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                0 -> if (resultCode == RESULT_OK && R.attr.data != null) {
                    val selectedImage = data?.getExtras()?.get("data") as Bitmap
                    profileImageView.setImageBitmap(selectedImage)
                }
                1 -> if (resultCode == RESULT_OK) {
                    val selectedImage: Uri = data!!.getData()!!
                    Log.d("PROFILE__","${convertMediaUriToPath(selectedImage)}")
                    profileImageView.setImageURI(selectedImage)

                    userManager.setCurrentProfileImage(selectedImage.toString())
                    viewModel.uploadUriResult(this!!.convertMediaUriToPath(selectedImage)!!)
                }

            }
        }


    }

    private fun selectImage(context: Context) {
        val options =
            arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Choose your profile picture")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == "Take Photo") {
                val takePicture =
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, 0)
            } else if (options[item] == "Choose from Gallery") {
                val pickPhoto = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(pickPhoto, 1)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()
    }


    private fun checkSelfReadStoragePermission() {
        if (ActivityCompat.checkSelfPermission(
                this.context!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_STORAGE_PERMISSION_REQUEST_CODE
            );

        }
    }

    fun convertMediaUriToPath(uri: Uri?): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = context!!.contentResolver.query(uri!!, proj, null, null, null)!!
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val path: String = cursor.getString(column_index)
        cursor.close()
        return path
    }
}
