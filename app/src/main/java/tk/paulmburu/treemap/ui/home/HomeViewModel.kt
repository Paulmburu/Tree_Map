package tk.paulmburu.treemap.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.MetadataChanges
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import tk.paulmburu.treemap.models.Arborist
import tk.paulmburu.treemap.models.Tree
import tk.paulmburu.treemap.repository.FirestoreRepository

class HomeViewModel() : ViewModel() {

    private val TAG = "BUBA_HOME"
    val firebaseRepository = FirestoreRepository()

    private var arboristMap = HashMap<String, Arborist>()

    private val _arboristList = MutableLiveData<List<Arborist>>()
    val arboristList: LiveData<List<Arborist>>
        get() = _arboristList


    private val _loadingDataState = MutableLiveData<LoadingState>()
    val loadingDataState: LiveData<LoadingState>
        get() = _loadingDataState


    init {

        viewModelScope.launch {
            myFlowData().collect {
                Log.d(TAG, "My Flow data -> ${it}")
                arboristMap.put(
                    it.aborist_username,
                    Arborist(
                        it.aborist_username,
                        it.arborist_email,
                        "Planted " + it.tree_id + " trees",
                        "150"
                    )
                )

                _arboristList.value = arboristMap.values.toList()
            }
        }
    }

    fun myFlowData(): Flow<Tree> = callbackFlow {

        val subscription = firebaseRepository.getAllTrees()
            .addSnapshotListener(MetadataChanges.INCLUDE) { querySnapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen error", e)
                    _loadingDataState.value = LoadingError("ERROR !")
                    return@addSnapshotListener
                }
                for (change in querySnapshot!!.documentChanges) {
                    if (change.type == DocumentChange.Type.ADDED) {
                        Log.d(TAG, "New city: ${change.document.data}")
                        offer(change.document.toObject(Tree::class.java))
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
