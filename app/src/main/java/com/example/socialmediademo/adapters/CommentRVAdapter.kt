package com.example.socialmediademo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediademo.databinding.PostCommentBinding

class CommentRVAdapter(private var comments: List<String>): RecyclerView.Adapter<CommentRVAdapter.ItemViewHolder>() {
    class ItemViewHolder(val binding: PostCommentBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(PostCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val comment = comments[position]

        holder.binding.apply {
            tvPostCommentText.text = comment
        }
    }

    override fun getItemCount() = comments.size

    fun update(comments: List<String>){
        this.comments = comments
        notifyDataSetChanged()
    }
}