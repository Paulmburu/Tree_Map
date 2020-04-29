package tk.paulmburu.treemap.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MetadataChanges
import tk.paulmburu.treemap.R
import tk.paulmburu.treemap.databinding.ArboristItemBinding
import tk.paulmburu.treemap.databinding.FragmentHomeBinding
import tk.paulmburu.treemap.models.Arborist
import tk.paulmburu.treemap.models.Tree


class HomeFragment : Fragment() {

    companion object {
        var list = arrayListOf<Tree>()
        val arboristMap = HashMap<String, Arborist>()
    }

    private lateinit var viewModel: HomeViewModel
    private var viewModelAdapter: HomeAdapter? = null
    private val TAG = "BUBA_HOME"

    private var db = FirebaseFirestore.getInstance()
    private val settings = FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .build()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentHomeBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        db.firestoreSettings = settings

        readData(object : FirebaseCallback {
            override fun onCallback(list: List<Tree>) {
                for(i in 0 until list.size){
                    Log.d("$TAG ->username", "${list[i].aborist_username}")
                    arboristMap.put(list[i].aborist_username,Arborist(list[i].aborist_username,list[i].arborist_email,"Planted "+ list[i].tree_id+" trees","150"))
                }
            }
        })
        val valueList = ArrayList(arboristMap.values)

        viewModelAdapter = HomeAdapter()


        viewModelAdapter?.arborists = valueList

        binding.root.findViewById<RecyclerView>(R.id.recyclerViewArborists).apply {

            adapter = viewModelAdapter
            layoutManager = LinearLayoutManager(context)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        return binding.root
    }

    class HomeAdapter() : RecyclerView.Adapter<HomeViewHolder>() {

        var arborists: List<Arborist> = emptyList()
            set(value) {
                field = value
            notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
            val withDataBinding: ArboristItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                HomeViewHolder.LAYOUT,
                parent,
                false
            )
            return HomeViewHolder(withDataBinding)
        }

        override fun getItemCount() = arborists.size

        override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
            holder.viewdataBinding.also {
                it.arborist = arborists.get(position)
            }
        }
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
                        Log.d(TAG, "New city: ${change.document.data}")
                        HomeFragment.list.add(change.document.toObject(Tree::class.java))
                    }

                    val source = if (querySnapshot.metadata.isFromCache)
                        "local cache"
                    else
                        "server"
//                    Log.d("$TAG", "Data fetched from $source")
                }
//                Log.d("$TAG ->list", "${HomeFragment.list}")
                firebaseCallback.onCallback(list)
            }
    }
}


class HomeViewHolder(val viewdataBinding: ArboristItemBinding) :
    RecyclerView.ViewHolder(viewdataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.arborist_item
    }
}


interface FirebaseCallback {
    fun onCallback(list: List<Tree>)
}