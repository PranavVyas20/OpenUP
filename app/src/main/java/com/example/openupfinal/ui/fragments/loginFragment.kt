package com.example.open_up.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.open_up.ui.activities.HomeActivity
import com.example.openupfinal.databinding.FragmentLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch

class loginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    lateinit var auth: FirebaseAuth
    private val binding get() = _binding!! // Use this !!!!!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        // Initialise firebase auth ref
        auth = FirebaseAuth.getInstance()


        // Navigate to Register Fragment
        binding.gotoRegister.setOnClickListener {
            // Handling the backstack
            if(findNavController().previousBackStackEntry != null){
                findNavController().popBackStack()
            }
            else{
                findNavController().navigate(loginFragmentDirections.actionLoginFragmentToRegisterFragment2())
            }
        }
        // Login user
        binding.btnLogin.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            loginUser(email, password)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Function to login user
    private fun loginUser(email:String,password:String){
        CoroutineScope(Dispatchers.IO).launch {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                OnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(activity,"Success", Toast.LENGTH_SHORT).show()
                        goToHomeActivity()
                    }else{
                        Toast.makeText(activity,"failed", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    // Go to home activity
    private fun goToHomeActivity(){
        val intent = Intent(activity, HomeActivity::class.java)
        // Clear back stack
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}