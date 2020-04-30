package tk.paulmburu.treemap.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tk.paulmburu.moviesreview.database.TreesDatabase
import tk.paulmburu.treemap.database.DatabaseTrees
import tk.paulmburu.treemap.models.Tree

class TreesRepository(private val database: TreesDatabase) {

    companion object{
        var result:LiveData<ArrayList<Tree>> = MutableLiveData()
    }

    val TAG: String  = "TREES_REPOSITORY"
    val firebaseRepository = FirestoreRepository()
    /**
     * A list of trees that can be added as markers.
     */
    val trees: LiveData<List<DatabaseTrees>> =database.treesDao.getTrees()



    suspend fun refreshTrees(){
        withContext(Dispatchers.IO){

            firebaseRepository.getAllGlobalTrees().
                addOnCompleteListener {
                        task ->
                    if(task.isSuccessful){
                        for (document: QueryDocumentSnapshot in task.result!!){
                            val tree = document.toObject(Tree::class.java)
                            result.value!!.add(tree)
//                            Log.d("BUBA","$tree")
                        }
                    }

                }
            Log.d("BUBA__","${result.value}")
//            database.treesDao.deleteAllTrees()
//            database.treesDao.insertAll(*result.map { DatabaseTrees(
//                timeStampString = it.timeStampString,
//                tree_id = it.tree_id,
//                name = it.name,
//                tree_image = it.tree_image,
//                aborist_username = it.aborist_username,
//                arborist_email = it.arborist_email,
//                tree_geopoint = it.tree_geopoint,
//                treeDescription = it.treeDescription
//            ) }.toTypedArray()
//            )
        }

    }

}