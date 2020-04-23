package tk.paulmburu.treemap.ui.tree


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.GeoPoint
import tk.paulmburu.treemap.R
import tk.paulmburu.treemap.databinding.FragmentTreeBinding
import tk.paulmburu.treemap.models.Tree

/**
 * A simple [Fragment] subclass.
 */
class TreeFragment : Fragment() {

    private lateinit var treeName: TextInputEditText
    private lateinit var tree: Tree

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentTreeBinding.inflate(inflater)
        binding.setLifecycleOwner(this)

        val treeViewModel = ViewModelProviders.of(this)
            .get(TreeViewModel::class.java)



        treeName = binding.root.findViewById<TextInputEditText>(R.id.tree_name_text_input_id)

        binding.root.findViewById<Button>(R.id.submit_new_tree_id).setOnClickListener{
            tree = Tree("1",treeName.text.toString(),"icons8_tree_planting_48.png","Buba","paulmburu53@gmail.com",
                GeoPoint(1.0,1.0), System.currentTimeMillis()
            )

            treeViewModel.saveTreeToFirebase(tree)
        }



        return binding.root
    }


}
