package com.example.socialmediademo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewPostActivity : AppCompatActivity() {
    private lateinit var rvViewPostComments: RecyclerView
    private lateinit var rvAdapter: CommentRVAdapter

    private lateinit var btLike: Button
    private lateinit var btCommentSubmit: Button

    private lateinit var tvViewPostTitle: TextView
    private lateinit var tvViewPostText: TextView
    private lateinit var tvViewPostComments: TextView
    private lateinit var tvViewPostLikes: TextView

    private lateinit var comments: List<String>

    private var username: String? = null

    private var postId = 0
    private lateinit var post: Post
    private val apiInterface by lazy { APIClient().getClient()?.create(APIInterface::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_post)

        comments = listOf()

        val sharedPreferences = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        username = sharedPreferences.getString("username", null)
        Log.d("MAIN", "user: $username")

        rvViewPostComments = findViewById(R.id.rvViewPostComments)
        rvAdapter = CommentRVAdapter(comments)
        rvViewPostComments.adapter = rvAdapter
        rvViewPostComments.layoutManager = LinearLayoutManager(this)

        btLike = findViewById(R.id.btLike)
        btLike.setOnClickListener {
            if(username!=null){
                if(post.likes.contains(username!!)){
                    Toast.makeText(this, "You have already liked this post", Toast.LENGTH_LONG).show()
                }else{
                    apiInterface?.updatePost(
                        post.id,
                        Post(
                            0,
                            username!!,
                            post.title,
                            post.likes+", $username",  // cannot be blank?
                            post.text,
                            post.comments, // cannot be blank?
                        )
                    )!!.enqueue(object: Callback<Post> {
                        override fun onResponse(call: Call<Post>, response: Response<Post>) {
                            Log.d("MAIN", "Response: $response")
                        }
                        override fun onFailure(call: Call<Post>, t: Throwable) {
                            Toast.makeText(this@ViewPostActivity, "Something went wrong", Toast.LENGTH_LONG).show()
                        }
                    })
                    Toast.makeText(this, "Post liked", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this, "You must be logged in to like posts", Toast.LENGTH_LONG).show()
            }
        }
        btCommentSubmit = findViewById(R.id.btLeaveComment)

        postId = intent.getIntExtra("postId", 1)
        getPost(postId)

        tvViewPostTitle = findViewById(R.id.tvViewPostTitle)
        tvViewPostText = findViewById(R.id.tvViewPostText)

        tvViewPostComments = findViewById(R.id.tvViewPostComments)
        tvViewPostLikes = findViewById(R.id.tvViewPostLikes)

    }

    private fun getPost(postId: Int){
        var issue = false
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = apiInterface!!.getPost(postId)
                if(response.isSuccessful){
                    post = response.body()!!
                }else{
                    issue = true
                    Log.d("MAIN", "Unable to get data.")
                }
            }catch(e: Exception){
                issue = true
                Log.d("MAIN", "Exception: $e")
            }
            if(!issue){
                withContext(Dispatchers.Main){
                    tvViewPostTitle.text = post.title
                    tvViewPostText.text = post.text
                    tvViewPostComments.text = "Comments: ${handleComments(post.comments)}"
                    tvViewPostLikes.text = "Likes: ${handleLikes(post.likes)}"
                    rvAdapter.update(comments)
                }
            }
        }
    }

    private fun handleComments(commentsString: String): Int{
        if(commentsString.isNotEmpty()){
            comments = commentsString.split(",")
            val newCommentsList = ArrayList<String>()
            for(comment:String in comments){
                newCommentsList.add(comment)
            }
            comments = newCommentsList
            return commentsString.split(",").size
        }
        return 0
    }

    private fun handleLikes(likesString: String): Int{
        if(likesString.isNotEmpty()){return likesString.split(",").size}
        return 0
    }
}