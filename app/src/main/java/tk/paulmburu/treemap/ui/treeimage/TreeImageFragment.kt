package tk.paulmburu.treemap.ui.treeimage


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tk.paulmburu.treemap.R

/**
 * A simple [Fragment] subclass.
 */
class TreeImageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tree_image, container, false)
    }


}
