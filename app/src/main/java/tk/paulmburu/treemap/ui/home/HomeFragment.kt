package tk.paulmburu.treemap.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import tk.paulmburu.treemap.R
import tk.paulmburu.treemap.databinding.ArboristItemBinding
import tk.paulmburu.treemap.databinding.FragmentHomeBinding
import tk.paulmburu.treemap.models.Arborist


class HomeFragment : Fragment() {


    private lateinit var viewModel: HomeViewModel
    private var viewModelAdapter: HomeAdapter? = null
    private val TAG = "BUBA_HOME"

    private lateinit var loadingImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentHomeBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        loadingImageView = binding.root.findViewById(R.id.loading_imageview)

//        viewModel.loadingDataState.observe(this, Observer {
//            when(it){
//                is Loading -> {
//                    binding.root.findViewById<RecyclerView>(R.id.recyclerViewArborists).visibility = View.INVISIBLE
//                    loadingImageView.visibility = View.VISIBLE
//                }
//                is LoadingDone -> {
//                    loadingImageView.visibility = View.INVISIBLE
//                    binding.root.findViewById<RecyclerView>(R.id.recyclerViewArborists).visibility = View.VISIBLE
//                }
//                is LoadingError -> {
//                    loadingImageView.visibility = View.INVISIBLE
//                }
//            }
//        })


        viewModelAdapter = HomeAdapter()

        viewModel.arboristList.observe(viewLifecycleOwner, Observer {it ->
            viewModelAdapter?.arborists = it
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<RecyclerView>(R.id.recyclerViewArborists).apply {

            adapter = viewModelAdapter
            layoutManager = LinearLayoutManager(context)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

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
}

class HomeViewHolder(val viewdataBinding: ArboristItemBinding) :
    RecyclerView.ViewHolder(viewdataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.arborist_item
    }
}


sealed class LoadingState
data class Loading(val loading: String): LoadingState()
data class LoadingDone(val done: String): LoadingState()
data class LoadingError(val error:String) : LoadingState()