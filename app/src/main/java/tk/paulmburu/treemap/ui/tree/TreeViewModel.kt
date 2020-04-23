package tk.paulmburu.treemap.ui.tree

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import tk.paulmburu.treemap.models.Tree
import tk.paulmburu.treemap.repository.FirestoreRepository

class TreeViewModel : ViewModel() {
    val TAG = "TREE_VIEW_MODEL"
    var firebaseRepository = FirestoreRepository()
    var savedTrees : MutableLiveData<List<Tree>> = MutableLiveData()

    // save address to firebase
    fun saveTreeToFirebase(newTree: Tree){
        firebaseRepository.saveNewTree(newTree).addOnFailureListener {
            Log.e(TAG,"Failed to save Address!")
        }
    }

    // get realtime updates from firebase regarding saved trees
    fun getSavedAddresses(): LiveData<List<Tree>> {
        firebaseRepository.getSavedAddress().addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                savedTrees.value = null
                return@EventListener
            }

            var savedTreesList : MutableList<Tree> = mutableListOf()
            for (doc in value!!) {
                var tree = doc.toObject(Tree::class.java)
                savedTreesList.add(tree)
            }
            savedTrees.value = savedTreesList
        })

        return savedTrees
    }

    // delete an address from firebase
    fun deleteAddress(tree: Tree){
        firebaseRepository.deleteAddress(tree).addOnFailureListener {
            Log.e(TAG,"Failed to delete Address")
        }
    }

}