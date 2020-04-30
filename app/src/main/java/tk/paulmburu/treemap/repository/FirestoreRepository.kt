package tk.paulmburu.treemap.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import tk.paulmburu.treemap.models.Arborist
import tk.paulmburu.treemap.models.Tree


class FirestoreRepository {

    val TAG = "FIREBASE_REPOSITORY"
    var firestoreDB = FirebaseFirestore.getInstance()
//    var user = FirebaseAuth.getInstance().currentUser
    
    // save new user to firestore
    fun createNewArborist(username: String, userEmail: String): Task<Void> {
        var documentReference = firestoreDB.collection("treemap_arborists").document(username+"_"+userEmail).
            collection("my_details").document("$username")
        return documentReference.set(Arborist(username,userEmail,"0","150"))
    }

    // save tree to firebase
    fun saveNewArboristTree(newTree: Tree, username: String, userEmail: String, treeId: String): Task<Void> {
        //var
        var documentReference = firestoreDB.collection("treemap_arborists").document(username+"_"+userEmail)
            .collection("trees_planted").document(treeId)
        return documentReference.set(newTree)
    }

    fun saveNewArboristTreeRegion(treeRegion: String,username: String, userEmail: String, treeId: String): Task<Void> {
        //var
        var documentReference = firestoreDB.collection("treemap_arborists").document(username+"_"+userEmail)
            .collection("trees_regions").document(System.currentTimeMillis().toString())
        return documentReference.set(treeRegion)
    }

    // add tree to global planted trees
    fun addNewArboristTreeToGlobalTrees(newTree: Tree): Task<Void> {
        var documentReference = firestoreDB.collection("global_planted_trees").document(System.currentTimeMillis().toString())
        return documentReference.set(newTree)
    }

     fun getAllGlobalTrees(): Task<QuerySnapshot?>{
        return try{
            val data = firestoreDB
                .collection("global_planted_trees")
                .get()

            data

        }catch (e: Exception){
            throw e
        }
    }

    // get saved trees from firebase
    fun getSavedAddress(): CollectionReference {
        var collectionReference = firestoreDB.collection("trees/paulmburu53@gmail.com/my_trees")
        return collectionReference
    }

    fun deleteAddress(tree: Tree): Task<Void> {
        var documentReference =  firestoreDB.collection("users/paulmburu53@gmail/my_trees")
            .document(tree.tree_id)

        return documentReference.delete()
    }

}
