package com.example.openupfinal.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.openupfinal.R
import com.example.openupfinal.data.Post
import com.example.openupfinal.data.User
import com.example.openupfinal.databinding.FragmentCreatePostBinding
import com.example.openupfinal.databinding.FragmentRegisterBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.time.LocalDateTime
import java.util.*
import kotlin.math.log

class createPostFragment : Fragment() {
    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!! // Use this !!!!!!
    lateinit var currentUser: User

    lateinit var postCollectionRef: CollectionReference
    val userCollectionRef = Firebase.firestore.collection("users")

    val auth = FirebaseAuth.getInstance()
    private var currentLoggedInUserid: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        currentUser = User()
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)

        currentLoggedInUserid = auth.currentUser?.uid
        postCollectionRef = Firebase.firestore.collection("posts")

        getCurrentUser()

        binding.createPostBtn.setOnClickListener {
            val postDes = binding.postDesETv.text.toString()
            val uniqueId = UUID.randomUUID().toString()
            val userName = currentUser.username
            val creationTime = System.currentTimeMillis()
            val userUID = currentUser.realUid
            val newPost = Post(uniqueId, creationTime, postDes, userName, userUID)
            uploadPostOnFirestore(newPost)
        }

        return binding.root
    }

        private fun getCurrentUser() {
            // get current user
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    userCollectionRef.document(auth.currentUser!!.uid)
                        .get()
                        .addOnSuccessListener {
                            currentUser = it.toObject(User::class.java)!!
                        }
                } catch (e: Exception) {
                    Log.d("Taggggggggg", "error")
                }
            }
        }

        private fun uploadPostOnFirestore(post: Post) {
            binding.apply {
                if (postDesETv.text?.isEmpty() == true) {
                    Toast.makeText(activity, "Please fill in the description", Toast.LENGTH_SHORT)
                        .show()
                    return
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                postCollectionRef.add(post).addOnCompleteListener(
                    OnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(activity, "Post created succesfully", Toast.LENGTH_SHORT)
                                .show()
                            findNavController()
                                .navigate(
                                    createPostFragmentDirections
                                        .actionCreatePostFragmentToHomeFeedFragment2()
                                )
                        } else {
                            Toast.makeText(activity, "Error creating post", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}