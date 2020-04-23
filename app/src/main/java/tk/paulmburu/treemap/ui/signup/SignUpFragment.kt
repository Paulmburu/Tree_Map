package tk.paulmburu.treemap.ui.signup


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import tk.paulmburu.treemap.MyApplication
import tk.paulmburu.treemap.R
import tk.paulmburu.treemap.databinding.FragmentSignUpBinding
import tk.paulmburu.treemap.ui.splashActivity.SplashActivity
import tk.paulmburu.treemap.ui.splashActivity.SplashActivityViewModel

/**
 * A simple [Fragment] subclass.
 */
class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var splashActivityViewModel: SplashActivityViewModel
    private lateinit var signUpViewModel: SignUpViewModel

    private lateinit var errorTextView: TextView
    private lateinit var usernameTextInput: EditText
    private lateinit var emailTextInput: EditText
    private lateinit var passwordTextInput: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application

        splashActivityViewModel = SplashActivityViewModel((application as MyApplication).userManager)
        signUpViewModel = SignUpViewModel()


        signUpViewModel.signupDetailsState.observe(this, Observer<SignupDetailsState> { state ->
            when(state){
                is SignupDetailsSuccess -> {
                    binding.root.findViewById<Button>(R.id.sign_up_button_id).visibility = View.INVISIBLE
                    binding.root.findViewById<ProgressBar>(R.id.progressBar_singup_id).visibility = View.VISIBLE
                    val username = usernameTextInput.text.toString()
                    val userEmail = emailTextInput.text.toString()
                    val password = passwordTextInput.text.toString()

                    splashActivityViewModel.updateUserData(username, userEmail ,password)
                    (activity as SplashActivity).onDetailsEntered()
                }
                is SignupDetailsError -> {
                    errorTextView.text = state.error
                    errorTextView.visibility = View.VISIBLE
                }

            }
        })
        setupViews()

        return binding.root
    }

    private fun setupViews() {
        errorTextView = binding.root.findViewById(R.id.error_tv_id)

        usernameTextInput = binding.root.findViewById(R.id.signup_username_textinput_id)
        usernameTextInput.doOnTextChanged { _, _, _, _ -> errorTextView.visibility = View.INVISIBLE }

        emailTextInput = binding.root.findViewById(R.id.signin_email_textinput_id)
        emailTextInput.doOnTextChanged { _, _, _, _ -> errorTextView.visibility = View.INVISIBLE }

        passwordTextInput = binding.root.findViewById(R.id.sigin_password_textinput_id)
        passwordTextInput.doOnTextChanged { _, _, _, _ -> errorTextView.visibility = View.INVISIBLE }

        binding.root.findViewById<Button>(R.id.sign_up_button_id).setOnClickListener {
            val username = usernameTextInput.text.toString()
            val userEmail = emailTextInput.text.toString()
            val password = passwordTextInput.text.toString()

            signUpViewModel.validateInput(username,userEmail,password)
        }
    }

}

sealed class SignupDetailsState
object  SignupDetailsSuccess : SignupDetailsState()
data class SignupDetailsError(val error: String) : SignupDetailsState()
