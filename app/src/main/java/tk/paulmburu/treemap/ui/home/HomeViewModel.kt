package tk.paulmburu.treemap.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MetadataChanges
import tk.paulmburu.treemap.models.Arborist
import tk.paulmburu.treemap.models.Tree

class HomeViewModel(): ViewModel() {
    private val _treesList = MutableLiveData<List<Tree>>()
    val treesList: LiveData<List<Tree>>
        get() = _treesList

    private val TAG = "BUBA_HOME"

    var db = FirebaseFirestore.getInstance()
    private val settings = FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .build()

    companion object {
        var list = arrayListOf<Tree>()
        var arboristList = arrayListOf<Arborist>()
    }


    fun readData(firebaseCallback: FirebaseCallback) {
        db.collection("global_planted_trees")
            .addSnapshotListener(MetadataChanges.INCLUDE) { querySnapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen error", e)
                    return@addSnapshotListener
                }
//                querySnapshot.documentChanges.forEach { documentChange -> list.add(documentChange.document.toObject()) }

                for (change in querySnapshot!!.documentChanges) {
                    if (change.type == DocumentChange.Type.ADDED) {
//                        Log.d(TAG, "New city: ${change.document.data}")
                        list.add(change.document.toObject(Tree::class.java))
                    }

                    val source = if (querySnapshot.metadata.isFromCache)
                        "local cache"
                    else
                        "server"
//                    Log.d("$TAG", "Data fetched from $source")
                }
                Log.d("$TAG ->list", "${HomeFragment.list}")
                firebaseCallback.onCallback(HomeFragment.list)
            }
    }

    init{

    }


    interface FirebaseCallback {
        fun onCallback(list: List<Tree>)
    }


}