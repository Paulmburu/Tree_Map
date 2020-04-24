package tk.paulmburu.treemap.ui.tree


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.GeoPoint
import tk.paulmburu.treemap.MyApplication
import tk.paulmburu.treemap.R
import tk.paulmburu.treemap.databinding.FragmentTreeBinding
import tk.paulmburu.treemap.models.Tree
import tk.paulmburu.treemap.ui.signin.SignInFragmentDirections
import tk.paulmburu.treemap.user.UserManager

/**
 * A simple [Fragment] subclass.
 */
class TreeFragment : Fragment() {

    private lateinit var treeName: TextInputEditText
    private lateinit var tree: Tree
    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentTreeBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application
        userManager = (application as MyApplication).userManager

        val treeViewModel = ViewModelProviders.of(this)
            .get(TreeViewModel::class.java)



        treeName = binding.root.findViewById<TextInputEditText>(R.id.tree_name_text_input_id)

        binding.root.findViewById<Button>(R.id.submit_new_tree_id).setOnClickListener{
            it.visibility = View.INVISIBLE
            binding.root.findViewById<ProgressBar>(R.id.add_tree_progressbar).visibility = View.VISIBLE
            tree = Tree("1",treeName.text.toString(),"icons8_tree_planting_48.png","Buba","paulmburu53@gmail.com",
                GeoPoint(1.0,1.0), System.currentTimeMillis(),"Tree by ${userManager.username}"
            )

            treeViewModel.saveTreeToFirebase(tree,userManager.username, userManager.userEmail,tree.tree_id)
        }

        treeViewModel._navigateToNiceWorkFragment.observe(this, Observer {
            findNavController().navigate(TreeFragmentDirections.actionTreeFragmentToNiceWorkFragment())
        })

        return binding.root
    }


}
