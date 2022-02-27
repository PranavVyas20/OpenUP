package com.example.openupfinal.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.openupfinal.MyDiffUtil
import com.example.openupfinal.R
import com.example.openupfinal.data.Post
import com.example.openupfinal.databinding.PostItemBinding
import kotlin.random.Random

class PostAdapter(val postList: MutableList<Post>,val context:Context):RecyclerView.Adapter<PostAdapter.MyViewHolder>() {

//    private lateinit var mListener: onItemClickListener
//
//    interface  onItemClickListener{
//        fun onItemClick(position:Int)
//    }
//    fun setOnItemClickListener(listener: onItemClickListener){
//        mListener = listener
//    }

    class MyViewHolder(val mBinding:PostItemBinding):RecyclerView.ViewHolder(mBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(PostItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentPost = postList[position]
        holder.mBinding.apply {
            postDesTv.text = currentPost.postDescription
            usernameTv.text = currentPost.postedByUser
            val time = DateUtils.getRelativeTimeSpanString(currentPost.creationTime).toString()
            timeTv.text = time
            userProfileTv.setBackgroundColor(generateRadomColor())
            userProfileTv.text = currentPost.postedByUser[0].toString()
        }
    }


    private fun generateRadomColor():Int{
        val rnd = Random.Default //kotlin.random
        val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        return color
    }
}