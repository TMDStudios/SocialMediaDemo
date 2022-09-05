package com.example.socialmediademo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediademo.databinding.PostCardBinding
import com.example.socialmediademo.models.Post

class RVAdapter(private var posts: List<Post>): RecyclerView.Adapter<RVAdapter.ItemViewHolder>() {
    class ItemViewHolder(val binding: PostCardBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(PostCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val post = posts[position]

        holder.binding.apply {
            tvPostTitle.text = post.title
        }
    }

    override fun getItemCount() = posts.size

    fun update(posts: List<Post>){
        this.posts = posts
        notifyDataSetChanged()
    }
}