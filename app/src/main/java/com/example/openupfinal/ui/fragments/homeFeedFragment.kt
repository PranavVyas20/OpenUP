package com.example.open_up.ui.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.open_up.ui.activities.HomeActivity
import com.example.open_up.ui.activities.LoginRegActivity
import com.example.openupfinal.adapters.PostAdapter
import com.example.openupfinal.data.Post
import com.example.openupfinal.data.User
import com.example.openupfinal.databinding.FragmentHomeFeedBinding
import com.example.openupfinal.ui.fragments.createPostFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class homeFeedFragment : Fragment() {
    private var _binding: FragmentHomeFeedBinding? = null
    private lateinit var auth: FirebaseAuth
    lateinit var postRef:Query
    lateinit var userRef: CollectionReference
    lateinit var postsList:MutableList<Post>
    private val binding get() = _binding!! // Use this !!!!!!
    lateinit var myAdapter:PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeFeedBinding.inflate(inflater, container, false)

        postsList = mutableListOf()

        auth = FirebaseAuth.getInstance()

        postRef = Firebase.firestore.collection("posts").orderBy("creationTime",Query.Direction.DESCENDING)

        userRef = Firebase.firestore.collection("users")

        getAllPosts()

        myAdapter = PostAdapter(postsList,requireContext())
        binding.postsRv.layoutManager = LinearLayoutManager(activity)
        binding.postsRv.adapter = myAdapter

        binding.changeThemeSwitch.setOnClickListener {
            if(binding.changeThemeSwitch.isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Sign out button listener
        binding.signoutBtn.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage("Log out ?")
            builder.setPositiveButton(("Yes"), DialogInterface.OnClickListener { dialogInterface, i ->
                goToMainActivity()
                dialogInterface.cancel()
            })
            builder.setNegativeButton(("No"), DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.cancel()
            })
            val alert = builder.create()
            alert.show()
        }

        return binding.root
    }

    private fun getAllPosts(){

        // get all posts with snap shot listener
        postRef.addSnapshotListener { querySnapshot, firestoreException ->
            firestoreException?.let {
                Toast.makeText(activity,it.message,Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            val postsSnap = querySnapshot!!.toObjects(Post::class.java)
            postsList.clear()
            postsList.addAll(postsSnap)
            myAdapter.notifyDataSetChanged()
            Log.d("All post",postsList.toString())
        }
    }

    private fun goToMainActivity(){
        auth.signOut()
        val intent = Intent(activity,LoginRegActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    }




