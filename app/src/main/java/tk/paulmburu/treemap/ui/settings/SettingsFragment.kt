package tk.paulmburu.treemap.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.firebase.ui.auth.AuthUI
import tk.paulmburu.treemap.R
import tk.paulmburu.treemap.databinding.FragmentSettingsBinding
import tk.paulmburu.treemap.ui.splashActivity.SplashActivity
import tk.paulmburu.treemap.utils.AuthenticationState
import tk.paulmburu.treemap.utils.UserInfo

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: SettingsFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this!!.activity!!)[SettingsFragmentViewModel::class.java]

        view.findViewById<TextView>(R.id.user_name_settings)
            .apply { setText(UserInfo.auth_username) }
        view.findViewById<TextView>(R.id.user_email_settings)
            .apply { setText(UserInfo.auth_email) }

        view.findViewById<ImageButton>(R.id.tm_signout).setOnClickListener {
            AuthUI.getInstance().signOut(this.context!!)
        }

        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when(authenticationState){
                AuthenticationState.UNAUTHENTICATED -> {
                    startActivity(Intent(activity,SplashActivity::class.java))
                }
            }
        })

        view.findViewById<ImageButton>(R.id.tm_apptheme_settings).setOnClickListener {
            view.findNavController()
                .navigate(SettingsFragmentDirections.actionSettingsFragmentToThemeSettings())
        }
    }
}

