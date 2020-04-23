package tk.paulmburu.treemap.ui.splashScreenFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tk.paulmburu.treemap.MyApplication
import tk.paulmburu.treemap.R
import tk.paulmburu.treemap.databinding.FragmentSplashScreenBinding
import tk.paulmburu.treemap.user.UserManager

class SplashScreenFragment : Fragment() {

    val TAG: String = "SplashScreenfragment"
    private lateinit var userManager: UserManager

    private lateinit var signInButton: Button
    private lateinit var signUpButton: Button

    val scope = CoroutineScope(Dispatchers.Main)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentSplashScreenBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application
        userManager = (application as MyApplication).userManager

        signInButton = binding.root.findViewById<Button>(R.id.button)
        signUpButton = binding.root.findViewById<Button>(R.id.splash_singup_button_id)

        scope.launch {
            delay(1000)
            if(userManager.username != ""){
                findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToSignInFragment())
            }else{
                findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToSignUpFragment())
            }

        }







        return binding.root
    }

}
