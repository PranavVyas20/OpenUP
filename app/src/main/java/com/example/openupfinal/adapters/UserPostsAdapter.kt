package com.example.openupfinal.adapters

import android.graphics.Color
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.openupfinal.data.Post
import com.example.openupfinal.databinding.PostItemBinding
import com.example.openupfinal.databinding.UserPostsItemBinding
import kotlin.random.Random

class UserPostsAdapter(private val userPostsList:MutableList<Post>):RecyclerView.Adapter<UserPostsAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: UserPostsItemBinding):RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(UserPostsItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.apply {
            val currentPost = userPostsList[position]
            holder.binding.apply {
                postDesTv.text = currentPost.postDescription
                usernameTv.text = currentPost.postedByUser
                val time = DateUtils.getRelativeTimeSpanString(currentPost.creationTime).toString()
                timeTv.text = time
                userProfileTv.setBackgroundColor(generateRadomColor())
                userProfileTv.text = currentPost.postedByUser[0].toString()
                deletePostBtn.setOnClickListener {
                    onItemClickListener?.let { it(currentPost) }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return userPostsList.size
    }
    private fun generateRadomColor():Int{
        val rnd = Random.Default //kotlin.random
        val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        return color
    }

    private var onItemClickListener: ((Post) -> Unit)? = null

    fun setOnItemClickListener(listener: (Post) -> Unit){
        onItemClickListener = listener
    }
}