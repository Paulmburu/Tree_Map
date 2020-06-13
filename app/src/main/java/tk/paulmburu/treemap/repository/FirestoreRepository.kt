package tk.paulmburu.treemap.repository

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.QuerySnapshot
import tk.paulmburu.treemap.models.Arborist
import tk.paulmburu.treemap.models.Region
import tk.paulmburu.treemap.models.Tree
import tk.paulmburu.treemap.utils.UserInfo


class FirestoreRepository {

    val TAG = "FIREBASE_REPOSITORY"
    var firestoreDB = FirebaseFirestore.getInstance()
    private val settings = FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .build();

//    val globalTrees: LiveData

    init {
        firestoreDB.firestoreSettings = settings
    }


    fun updateArboristDetails(username: String, userEmail: String, trees: String): Task<Void> {
        var documentReference =
            firestoreDB.collection("treemap_arborists").document(userEmail).collection("my_details")
                .document("$username")
        return documentReference.set(Arborist(username, userEmail, trees, "150"))
    }

    // save tree to firebase
    fun saveNewArboristTree(
        newTree: Tree,
        username: String,
        userEmail: String,
        treeId: String
    ): Task<Void> {
        //var
        var documentReference = firestoreDB.collection("treemap_arborists").document(userEmail)
            .collection("trees_planted").document(treeId)
        return documentReference.set(newTree)
    }

    fun saveNewArboristTreeRegion(
        treeRegion: String,
        username: String,
        userEmail: String,
        treeId: String
    ): Task<Void> {
        //var
        var documentReference = firestoreDB.collection("treemap_arborists").document(userEmail)
            .collection("trees_regions").document(System.currentTimeMillis().toString())
        return documentReference.set(Region(treeRegion))
    }

    // add tree to global planted trees
    fun addNewArboristTreeToGlobalTrees(newTree: Tree): Task<Void> {
        var documentReference = firestoreDB.collection("global_planted_trees")
            .document(System.currentTimeMillis().toString())
        return documentReference.set(newTree)
    }

    fun getAllTrees(): CollectionReference {
        return firestoreDB.collection("global_planted_trees")
    }

    fun getAllCurrentArboristTrees(): CollectionReference {
        return firestoreDB.collection("treemap_arborists").document(UserInfo.auth_email.toString())
            .collection("trees_planted")
    }

    fun getAllCurrentArboristTreesRegions(): CollectionReference {
        return firestoreDB.collection("treemap_arborists").document(UserInfo.auth_email.toString())
            .collection("trees_regions")
    }

    fun getAllGlobalTrees(): Task<QuerySnapshot?> {
        return try {
            val data = firestoreDB
                .collection("global_planted_trees")
                .get()

            data

        } catch (e: Exception) {
            throw e
        }
    }

    // get saved trees from firebase
    fun getSavedAddress(): CollectionReference {
        var collectionReference = firestoreDB.collection("trees/paulmburu53@gmail.com/my_trees")
        return collectionReference
    }

    fun deleteAddress(tree: Tree): Task<Void> {
        var documentReference = firestoreDB.collection("users/paulmburu53@gmail/my_trees")
            .document(tree.tree_id)

        return documentReference.delete()
    }


}
