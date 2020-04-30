package tk.paulmburu.treemap.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.change_password_dialog.view.*
import tk.paulmburu.treemap.MyApplication
import tk.paulmburu.treemap.R
import tk.paulmburu.treemap.databinding.FragmentSettingsBinding
import tk.paulmburu.treemap.ui.tree.TreeFragmentDirections
import tk.paulmburu.treemap.user.UserManager

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater)
        val application = requireNotNull(this.activity).application
        userManager = (application as MyApplication).userManager

        binding.root.findViewById<TextView>(R.id.user_name_settings)
            .apply { setText(userManager.username) }
        binding.root.findViewById<TextView>(R.id.user_email_settings)
            .apply { setText(userManager.userEmail) }

        binding.root.findViewById<ImageView>(R.id.tm_password_btn_settings).setOnClickListener {
            changePasswordDialog() 
        }

        return binding.root
    }

    fun changePasswordDialog() {
        //Inflate the dialog with custom view
        val mDialogView =
            LayoutInflater.from(this.context).inflate(R.layout.change_password_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this.context!!)
            .setView(mDialogView)
            .setTitle("Confirm Password")
        //show dialog
        val mAlertDialog = mBuilder.show()
        //login button click of custom layout
        mDialogView.dialog_change_btn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
            //get text from EditTexts of custom layout
            val oldPassword = mDialogView.dialogOldPasswordEt.text.toString()
            val newPassword = mDialogView.dialogNewPasswordEt.text.toString()
            val confirmNewPassword = mDialogView.dialogConfirmNewPasswEt.text.toString()

            if (oldPassword != userManager.password){
                mDialogView.errorTv.visibility = View.VISIBLE
            }else {
                if(newPassword != confirmNewPassword){
                    mDialogView.errorTv.apply {
                        text="New password doesn't match"
                        visibility = View.VISIBLE
                    }
                }else if(newPassword == confirmNewPassword){
                    userManager.changePassword(newPassword)
                }
            }

            //cancel button click of custom layout
            mDialogView.dialogCancelBtn.setOnClickListener {
                //dismiss dialog
                mAlertDialog.dismiss()
            }
        }
    }
}
