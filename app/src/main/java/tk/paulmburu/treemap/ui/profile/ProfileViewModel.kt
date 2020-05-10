package tk.paulmburu.treemap.ui.profile


import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import tk.paulmburu.treemap.user.UserManager
import java.io.File

class ProfileViewModel(userManager: UserManager): ViewModel() {

    val userManager = userManager

    val storage = Firebase.storage
    var storageRef = storage.reference

    fun uploadUriResult(currentImageUri: String){

        // File or Blob
        val file = Uri.fromFile(File(currentImageUri))

        // Create the file metadata
        val metadata = storageMetadata {
            contentType = "image/jpeg"
        }

        // Upload file and metadata to the path 'images/mountains.jpg'
        var uploadTask = storageRef.child("\"${userManager.username}_${userManager.userEmail}/trees/profile_image").putFile(file, metadata)

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            println("Upload is $progress% done")
            Log.d("BUBA_PROFILE","Upload is $progress% done")
        }.addOnPausedListener {
            println("Upload is paused")
        }.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            // Handle successful uploads on complete
            // ...
            Log.d("BUBA_PROFILE","DONE")
        }
    }

    class Factory(val userManager: UserManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(userManager) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}