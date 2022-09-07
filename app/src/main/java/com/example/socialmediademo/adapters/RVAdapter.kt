package com.example.socialmediademo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediademo.MainActivity
import com.example.socialmediademo.databinding.PostCardBinding
import com.example.socialmediademo.models.Post

class RVAdapter(val activity: MainActivity, private var posts: List<Post>): RecyclerView.Adapter<RVAdapter.ItemViewHolder>() {
    class ItemViewHolder(val binding: PostCardBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(PostCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val post = posts[position]

        holder.binding.apply {
            tvPostTitle.text = post.title
            if(post.comments.isNotEmpty()){
                tvComments.text = "${post.comments.split(",").size} Comment(s)"
            }
            if(post.likes.isNotEmpty()){
                tvLikes.text = "${post.likes.split(",").size} Likes"
            }
            tvOpenThread.setOnClickListener {
                activity.viewPost(post.id)
            }
        }
    }

    override fun getItemCount() = posts.size

    fun update(posts: List<Post>){
        this.posts = posts
        notifyDataSetChanged()
    }
}