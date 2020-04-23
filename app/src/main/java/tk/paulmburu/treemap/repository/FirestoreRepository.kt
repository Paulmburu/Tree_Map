package tk.paulmburu.treemap.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import tk.paulmburu.treemap.models.Tree


class FirestoreRepository {

    val TAG = "FIREBASE_REPOSITORY"
    var firestoreDB = FirebaseFirestore.getInstance()
//    var user = FirebaseAuth.getInstance().currentUser


    // save tree to firebase
    fun saveNewTree(newTree: Tree): Task<Void> {
        //var
        var documentReference = firestoreDB.collection("trees").document("paulmburu53@gmail.com")
            .collection("my_trees").document(newTree.tree_id)
        return documentReference.set(newTree)
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
