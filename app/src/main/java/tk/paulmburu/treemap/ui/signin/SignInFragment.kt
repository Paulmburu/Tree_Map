package tk.paulmburu.treemap.ui.signin


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import tk.paulmburu.treemap.MyApplication
import tk.paulmburu.treemap.R
import tk.paulmburu.treemap.databinding.FragmentSignInBinding
import tk.paulmburu.treemap.ui.splashActivity.SplashActivity

/**
 * A simple [Fragment] subclass.
 */
class SignInFragment : Fragment() {
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var errorTextView: TextView
    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application

        signInViewModel = SignInViewModel((application as MyApplication).userManager)
        signInViewModel.signinState.observe(
            this,
            androidx.lifecycle.Observer<SigninViewState> { state ->
                when (state) {
                    is SigninSuccess -> {
                        binding.root.findViewById<Button>(R.id.sign_in_button_id).visibility = View.INVISIBLE
                        binding.root.findViewById<ProgressBar>(R.id.progressBar_signin_id).visibility = View.VISIBLE
                        (activity as SplashActivity).onDetailsEntered()
                    }
                    is SigninError -> errorTextView.visibility = View.VISIBLE
                }
            })
        errorTextView = binding.root.findViewById(R.id.error_tv_id)
        setupViews()

        return binding.root
    }

    private fun setupViews() {
        val usernameTextInput = binding.root.findViewById<TextInputEditText>(R.id.signin_username_textinput_id)
        usernameTextInput.isEnabled = false
        usernameTextInput.setText(signInViewModel.getUsername())

        val emailTextInput = binding.root.findViewById<TextInputEditText>(R.id.signin_email_textinput_id)
        emailTextInput.isEnabled = false
        emailTextInput.setText(signInViewModel.getUserEmail())

        val passwordTextInput = binding.root.findViewById<TextInputEditText>(R.id.sigin_password_textinput_id)
        passwordTextInput.doOnTextChanged { _, _, _, _ -> errorTextView.visibility = View.INVISIBLE }

        binding.root.findViewById<Button>(R.id.sign_in_button_id).setOnClickListener { view ->
            Log.d(this.toString(), "username := ${signInViewModel.getUsername()}")
            Log.d(this.toString(), "use Email := ${signInViewModel.getUserEmail()}")
            signInViewModel.signin(usernameTextInput.text.toString(), emailTextInput.text.toString(), passwordTextInput.text.toString())
        }

        binding.root.findViewById<Button>(R.id.unregister_button_id).setOnClickListener { view ->
            signInViewModel.unregister()

            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
        }
    }
}

sealed class SigninViewState
object SigninSuccess : SigninViewState()
object SigninError : SigninViewState()
