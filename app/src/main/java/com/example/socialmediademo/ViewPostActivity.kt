package com.example.socialmediademo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediademo.adapters.CommentRVAdapter
import com.example.socialmediademo.api.APIClient
import com.example.socialmediademo.api.APIInterface
import com.example.socialmediademo.models.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewPostActivity : AppCompatActivity() {
    private lateinit var rvViewPostComments: RecyclerView
    private lateinit var rvAdapter: CommentRVAdapter

    private lateinit var btLike: Button
    private lateinit var btCommentSubmit: Button

    private lateinit var tvViewPostTitle: TextView
    private lateinit var tvViewPostText: TextView

    private lateinit var comments: List<String>

    private var postId = 0
    private lateinit var post: Post
    private val apiInterface by lazy { APIClient().getClient()?.create(APIInterface::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_post)

        comments = listOf("THIS IS GLORIOUS!", "hello good buddy", "COMMENT!")

        rvViewPostComments = findViewById(R.id.rvViewPostComments)
        rvAdapter = CommentRVAdapter(comments)
        rvViewPostComments.adapter = rvAdapter
        rvViewPostComments.layoutManager = LinearLayoutManager(this)

        btLike = findViewById(R.id.btLike)
        btCommentSubmit = findViewById(R.id.btLeaveComment)

        postId = intent.getIntExtra("postId", 1)
        getPost(postId)

        tvViewPostTitle = findViewById(R.id.tvViewPostTitle)
        tvViewPostText = findViewById(R.id.tvViewPostText)

    }

    private fun getPost(postId: Int){
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = apiInterface!!.getPost(postId)
                if(response.isSuccessful){
                    post = response.body()!!
                    tvViewPostTitle.text = post.title
                    tvViewPostText.text = post.text
                }else{
                    Log.d("MAIN", "Unable to get data.")
                }
            }catch(e: Exception){
                Log.d("MAIN", "Exception: $e")
            }
            withContext(Dispatchers.Main){
                rvAdapter.update(comments)
            }
        }
    }
}