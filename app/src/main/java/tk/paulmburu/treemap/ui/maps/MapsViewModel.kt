package tk.paulmburu.treemap.ui.maps

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import tk.paulmburu.treemap.models.Tree
import tk.paulmburu.treemap.repository.FirestoreRepository

class MapsViewModel(): ViewModel() {
    val TAG = "MAPS_VIEW_MODEL"
    val firebaseRepository = FirestoreRepository()
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )


    var firestoreDB = FirebaseFirestore.getInstance()
    val query = firestoreDB.collection("cities")

    val _trees: MutableLiveData<List<Tree>> = MutableLiveData()
    val tree: LiveData<List<Tree>>
        get() = _trees

    suspend fun getAllTrees(){
        firebaseRepository.getAllGlobalTrees().
            addOnCompleteListener {
                task ->
            var list: ArrayList<Tree> = ArrayList<Tree>()
            if(task.isSuccessful){
                for (document: QueryDocumentSnapshot in task.result!!){
                    list.add(document.toObject(Tree::class.java))
                    val tree = document.toObject(Tree::class.java)
                    Log.d(TAG,"$tree")
                }
            }

            _trees.value = list
        }
    }

}