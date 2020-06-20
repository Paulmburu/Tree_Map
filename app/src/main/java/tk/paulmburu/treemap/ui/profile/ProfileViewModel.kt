package tk.paulmburu.treemap.ui.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import tk.paulmburu.treemap.models.Region
import tk.paulmburu.treemap.models.Tree
import tk.paulmburu.treemap.repository.FirestoreRepository
import tk.paulmburu.treemap.ui.home.LoadingError
import tk.paulmburu.treemap.user.UserManager
import tk.paulmburu.treemap.utils.UserInfo
import java.io.File


class ProfileViewModel() : ViewModel() {

    val TAG = "ProfileViewModel"

    private val _status = MutableLiveData<ProfileImageLoadingState>()
    val status: LiveData<ProfileImageLoadingState>
        get() = _status

    private val _treesPlanted = MutableLiveData<String>()
    val treesPlanted: LiveData<String>
        get() = _treesPlanted

    private val _treesRegions = MutableLiveData<MutableList<Region>>()
    val treesRegions: LiveData<MutableList<Region>>
        get() = _treesRegions

    val firebaseRepository = FirestoreRepository()

    val storage = Firebase.storage
    var storageRef = storage.reference

    fun uploadUriResult(absoluteImagePath: String) {

        // File or Blob
        val file = Uri.fromFile(File(absoluteImagePath))

        // Create the file metadata
        val metadata = storageMetadata {
            contentType = "image/jpeg"
        }

        // Upload file and metadata to the path 'images/mountains.jpg'
        var uploadTask = storageRef.child("${UserInfo.auth_username}/trees/profile_image")
            .putFile(file, metadata)

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
            Log.d("BUBA_PROFILE", "DONE")
            _status.value = LoadingProfileImageDone
        }
    }

    init {
        getNumberOfTreesPlanted()

        viewModelScope.launch {
            getArboristTreesRegions().collect {
                Log.d(TAG, "Collected data ${it}")
                treesRegions.value?.add(it)
            }
        }
    }

    fun getNumberOfTreesPlanted() {
        firebaseRepository.getAllCurrentArboristTrees().get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.size()}")
                    _treesPlanted.value = document.size().toString()
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

    }

    fun getArboristTreesRegions(): Flow<Region> = callbackFlow {

        val subscription = firebaseRepository.getAllCurrentArboristTreesRegions()
            .addSnapshotListener(MetadataChanges.INCLUDE) { querySnapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen error", e)
                    return@addSnapshotListener
                }
                for (change in querySnapshot!!.documentChanges) {
                    if (change.type == DocumentChange.Type.ADDED) {
                        Log.d(TAG, "New city: ${change.document.data}")
                        offer(change.document.toObject(Region::class.java))
                    }
                    val source = if (querySnapshot.metadata.isFromCache)
                        "local cache"
                    else
                        "server"
                    Log.d("$TAG", "Data fetched from $source")
                }
            }
        //Finally if collect is not in use or collecting any data we cancel this channel to prevent
        // any leak and remove the subscription listener to the database
        awaitClose { subscription.remove() }
    }
}

sealed class ProfileImageLoadingState
data class LoadingProfileImage(val percentage: String) : ProfileImageLoadingState()
object LoadingProfileImageDone : ProfileImageLoadingState()
object LoadingProfileImageError : ProfileImageLoadingState()