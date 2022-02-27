package com.example.open_up.ui.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.icu.lang.UCharacter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.openupfinal.adapters.UserPostsAdapter
import com.example.openupfinal.data.Post
import com.example.openupfinal.data.User
import com.example.openupfinal.databinding.FragmentUserProfileBinding
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
import kotlin.random.Random


class userProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!! // Use this !!!!!!
    private lateinit var myAdapter:UserPostsAdapter
    private lateinit var db:FirebaseFirestore
    private lateinit var userPostsList: MutableList<Post>
    private lateinit var usersReference: CollectionReference
    private lateinit var postsRefQuery:Query
    private lateinit var postsRef:CollectionReference
    private lateinit var postRefDelQuery:Query
    private lateinit var currentLoggedInUser: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)

        currentLoggedInUser = User()
        userPostsList = mutableListOf()

        // Db reference
         db = FirebaseFirestore.getInstance()
        userPostsList = mutableListOf()
        usersReference = db.collection("users")

        postsRef = Firebase.firestore.collection("posts")

        postsRefQuery = Firebase.firestore
            .collection("posts")
            .whereEqualTo("postedByUserUID",FirebaseAuth.getInstance().currentUser!!.uid)

        // Setting up the adapter
        myAdapter = UserPostsAdapter(userPostsList)
        binding.userPostRV.adapter = myAdapter
        binding.userPostRV.layoutManager = LinearLayoutManager(activity)

        // Get current user
        getCurrentUser()

        // Get all user's post
        getUserPosts()

        // on item click listener on the user post adapter
        myAdapter.setOnItemClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Confirm delete")
            builder.setMessage("Are you sure you want to delte this post ?")
            builder.setPositiveButton(("Yes"),DialogInterface.OnClickListener { dialogInterface, i ->
                deletePost(it)
                Toast.makeText(activity,"Post deleted",Toast.LENGTH_SHORT).show()
                dialogInterface.cancel()
            })
            builder.setNegativeButton(("No"),DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.cancel()
            })
            val alert = builder.create()
            alert.show()
        }
        return binding.root
    }

    // get users all posts
    private fun getUserPosts(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                postsRefQuery.addSnapshotListener { snapshot, error ->
                    error?.let {
                        Log.d("Taggggg","error fetching user's posts")
                        return@addSnapshotListener
                    }
                    val postSnap = snapshot!!.toObjects(Post::class.java)
                    userPostsList.clear()
                    userPostsList.addAll(postSnap)
                    myAdapter.notifyDataSetChanged()
                    Log.d("Taggggg",userPostsList.toString())
                }
            }catch (e:Exception){
//                Toast.makeText(activity,"Some error occured",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateRadomColor():Int{
        val rnd = Random.Default //kotlin.random
        val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        return color
    }

    private fun getCurrentUser(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                usersReference.document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .get()
                    .addOnSuccessListener {
                        currentLoggedInUser = it.toObject(User::class.java)!!
                        binding.userProfileFirstLetterTv.text = currentLoggedInUser.username[0].toString()
                        binding.ekdamrandom.text = currentLoggedInUser.username
                        Log.d("Taggggggg",currentLoggedInUser.username)
                    }
            }catch (e:Exception){
                Log.d("Taggggggg","error fetching current logged in user")
            }
        }
    }

    private fun deletePost(post:Post){
        postRefDelQuery = Firebase.firestore
            .collection("posts")
            .whereEqualTo("id",post.id)

        CoroutineScope(Dispatchers.IO).launch {
            val postToDelResponse =  postRefDelQuery.get().await()

            for(recPost in postToDelResponse){
                try {
                    postsRef.document(recPost.id).delete().await()
                }catch (e:Exception){
                    Log.d("Tagggggggg","Error in deleting the post")
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}