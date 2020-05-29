package tk.paulmburu.treemap.ui.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import tk.paulmburu.treemap.user.UserManager
import java.io.File


class ProfileViewModel(userManager: UserManager): ViewModel() {

    private val _status = MutableLiveData<ProfileImageLoadingState>()
    val status: LiveData<ProfileImageLoadingState>
        get() = _status

    val userManager = userManager

    val storage = Firebase.storage
    var storageRef = storage.reference

    fun uploadUriResult(absoluteImagePath: String){

        // File or Blob
        val file = Uri.fromFile(File(absoluteImagePath))

        // Create the file metadata
        val metadata = storageMetadata {
            contentType = "image/jpeg"
        }

        // Upload file and metadata to the path 'images/mountains.jpg'
        var uploadTask = storageRef.child("${userManager.username}_${userManager.userEmail}/trees/profile_image").putFile(file, metadata)

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            _status.value = LoadingProfileImage(progress.toInt().toString())
            println("Upload is $progress% done")
        }.addOnPausedListener {
            println("Upload is paused")
        }.addOnFailureListener {
            // Handle unsuccessful uploads
            _status.value = LoadingProfileImageError
        }.addOnSuccessListener {
            // Handle successful uploads on complete
            Log.d("BUBA_PROFILE","DONE")
            _status.value = LoadingProfileImageDone
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

sealed class ProfileImageLoadingState
data class LoadingProfileImage(val percentage: String) : ProfileImageLoadingState()
object LoadingProfileImageDone : ProfileImageLoadingState()
object LoadingProfileImageError : ProfileImageLoadingState()