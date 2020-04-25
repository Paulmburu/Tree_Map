package tk.paulmburu.treemap.ui.profile


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import tk.paulmburu.treemap.MyApplication
import tk.paulmburu.treemap.R
import tk.paulmburu.treemap.databinding.FragmentProfileBinding
import tk.paulmburu.treemap.user.UserManager

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application
        userManager = (application as MyApplication).userManager

        binding.root.findViewById<TextView>(R.id.user_name_id).apply {
            setText(userManager.username)
        }

        userManager.treesPlantedByUser.apply {
            binding.root.findViewById<TextView>(R.id.user_planted_trees_id).setText(this)
            binding.root.findViewById<TextView>(R.id.total_trees_planted_tv_id).setText(this)
        }
        return binding.root
    }


}
