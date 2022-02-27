package com.example.open_up.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.open_up.ui.activities.HomeActivity
import com.example.openupfinal.data.User
import com.example.openupfinal.databinding.FragmentRegisterBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.openupfinal.UsernameList
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class registerFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    lateinit var auth:FirebaseAuth
    private lateinit var userCollectionRef:CollectionReference
    private val binding get() = _binding!! // Use this !!!!!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()


        userCollectionRef = Firebase.firestore.collection("users")

        val firebaseUser = auth.currentUser

        if(firebaseUser!=null){
            goToHomeActivity()
        }

        // Navigate to login fragment
        binding.goToLoginTv.setOnClickListener {
            if(findNavController().previousBackStackEntry != null){
                findNavController().popBackStack()
            }else{
                findNavController().navigate(registerFragmentDirections
                    .actionRegisterFragmentToLoginFragment2())
            }
        }

        // Generate random username
        binding.genRandomUsernameTv.setOnClickListener {
           binding.inputUsername.setText(generateRandomUsername())
        }

        // Register user
        binding.btnRegister.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            val username = binding.inputUsername.text.toString()
           registerUser(email,password, username)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Function to register user
    private fun registerUser(email:String,password:String,username:String){
        CoroutineScope(Dispatchers.IO).launch {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                OnCompleteListener {
                    if(it.isSuccessful){
                        // if registered succesfully then save the user in the db as well
                        saveUserToDb(username,email,auth.currentUser!!.uid)
                        Toast.makeText(activity,"Success",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(activity,"failed",Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    // Function to generate random username
    private fun generateRandomUsername(): String {
        val randomInt = (0..195).random()
        return UsernameList.randomNamesList[randomInt]
    }

    // Function to save user to firestore
    private fun saveUserToDb(username: String,email:String,uid:String){
        val newUser = User(username,email,uid)
        userCollectionRef.document(auth.currentUser!!.uid).set(newUser).addOnCompleteListener(OnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(activity,"User saved in db",Toast.LENGTH_SHORT).show()
                // when user saved in the db then navigate to Home Activity
                goToHomeActivity()
            }
            else{
                Toast.makeText(activity,"Error saving in db",Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Function to go to home activity with intent
    private fun goToHomeActivity(){
        val intent = Intent(activity, HomeActivity::class.java)
        // Clear back stack
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}