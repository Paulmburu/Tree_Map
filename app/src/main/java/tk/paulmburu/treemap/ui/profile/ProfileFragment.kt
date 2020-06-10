package tk.paulmburu.treemap.ui.profile


import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView
import tk.paulmburu.treemap.MyApplication
import tk.paulmburu.treemap.R
import tk.paulmburu.treemap.databinding.FragmentProfileBinding
import tk.paulmburu.treemap.user.UserManager
import tk.paulmburu.treemap.utils.UserInfo
import tk.paulmburu.treemap.utils.showSnackbar


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

        val lineChart = binding.root.findViewById<LineChart>(R.id.chart)

//        val mTf: Typeface = Typeface.createFromAsset(assets, "OpenSans-Bold.ttf")
        val data = getData(userManager.treesPlantedByUser.toInt(), 100F)
        setupChart(lineChart, data!!, Color.rgb(137, 230, 81))

        viewModel = ViewModelProviders.of(this)
            .get(ProfileViewModel::class.java)

        viewModel.treesPlanted.observe(viewLifecycleOwner, Observer {
            userManager.updateTreesPlantedCount(it)
            updateTreesPlanted()
        })

        viewModel.treesRegions.observe(viewLifecycleOwner, Observer {
            val regions = it.joinToString {
                ",";
                " ";
                " ";
                3;
                "..."
            }

            binding.root.findViewById<TextView>(R.id.areas_covered_tv_id).setText(regions)
        })

        viewModel.status.observe(viewLifecycleOwner, Observer {
            when (it) {
                is LoadingProfileImageDone -> {
                    binding.root.findViewById<ImageView>(R.id.loading_profile_image).visibility =
                        View.INVISIBLE
                    showSnackbar(
                        binding.root.findViewById(R.id.profile_constraint_layout),
                        "Image uploaded successfully"
                    )
                }

                is LoadingProfileImage -> {
                    binding.root.findViewById<ImageView>(R.id.loading_profile_image).visibility =
                        View.VISIBLE
                    showSnackbar(
                        binding.root.findViewById(R.id.profile_constraint_layout),
                        "Upload is at ${it.percentage}%"
                    )
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

        updateTreesPlanted()
        return binding.root
    }

    fun updateTreesPlanted() {
        userManager.treesPlantedByUser.apply {
            binding.root.findViewById<TextView>(R.id.user_planted_trees_id).setText(this)
            binding.root.findViewById<TextView>(R.id.total_trees_planted_tv_id).setText(this)
        }
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
                    Log.d("PROFILE__", "${convertMediaUriToPath(selectedImage)}")
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

    private fun setupChart(chart: LineChart, data: LineData, color: Int) {
        (data.getDataSetByIndex(0) as LineDataSet).setCircleHoleColor(color)

        // no description text
        chart.getDescription().setEnabled(false)

        // chart.setDrawHorizontalGrid(false);
        //
        // enable / disable grid background
        chart.setDrawGridBackground(false)
        //        chart.getRenderer().getGridPaint().setGridColor(Color.WHITE & 0x70FFFFFF);

        // enable touch gestures
        chart.setTouchEnabled(true)

        // enable scaling and dragging
        chart.setDragEnabled(true)
        chart.setScaleEnabled(true)

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false)
        chart.setBackgroundColor(color)

        // set custom chart offsets (automatic offset calculation is hereby disabled)
        chart.setViewPortOffsets(10F, 0F, 10F, 0F)

        // add data
        chart.setData(data)

        // get the legend (only possible after setting data)
        val l: Legend = chart.getLegend()
        l.setEnabled(false)
        chart.getAxisLeft().setEnabled(false)
        chart.getAxisLeft().setSpaceTop(40F)
        chart.getAxisLeft().setSpaceBottom(40F)
        chart.getAxisRight().setEnabled(false)
        chart.getXAxis().setEnabled(false)

        // animate calls invalidate()...
        chart.animateX(2500)
    }

    private fun getData(count: Int, range: Float): LineData? {
        val values: ArrayList<Entry> = ArrayList()
        for (i in 0 until count) {
//            val entryValue = (Math.random() * range).toFloat() + 3
            values.add(Entry(i.toFloat(), i.toFloat()))
        }

        // create a dataset and give it a type
        val set1 = LineDataSet(values, "DataSet 1")
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);
        set1.setLineWidth(1.75f)
        set1.setCircleRadius(5f)
        set1.setCircleHoleRadius(2.5f)
        set1.setColor(Color.WHITE)
        set1.setCircleColor(Color.WHITE)
        set1.setHighLightColor(Color.WHITE)
        set1.setDrawValues(false)

        // create a data object with the data sets
        return LineData(set1)
    }
}
