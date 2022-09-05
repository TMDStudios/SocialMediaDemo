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
    private lateinit var tvViewPostComments: TextView
    private lateinit var tvViewPostLikes: TextView

    private lateinit var comments: List<String>

    private var postId = 0
    private lateinit var post: Post
    private val apiInterface by lazy { APIClient().getClient()?.create(APIInterface::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_post)

        comments = listOf()

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

        tvViewPostComments = findViewById(R.id.tvViewPostComments)
        tvViewPostLikes = findViewById(R.id.tvViewPostLikes)

    }

    private fun getPost(postId: Int){
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = apiInterface!!.getPost(postId)
                if(response.isSuccessful){
                    post = response.body()!!
                }else{
                    Log.d("MAIN", "Unable to get data.")
                }
            }catch(e: Exception){
                Log.d("MAIN", "Exception: $e")
            }
            withContext(Dispatchers.Main){
                tvViewPostTitle.text = post.title
                tvViewPostText.text = post.text
                tvViewPostComments.text = "Comments: ${handleComments(post.comments)}"
                tvViewPostLikes.text = "Likes: ${handleLikes(post.likes)}"
                rvAdapter.update(comments)
            }
        }
    }

    private fun handleComments(commentsString: String): Int{
        comments = commentsString.split(",")
        var numberOfComments = 0
        val newCommentsList = ArrayList<String>()
        for(comment:String in comments){
            numberOfComments++
            newCommentsList.add(comment)
        }
        comments = newCommentsList
        if (numberOfComments==0 && commentsString.isNotEmpty()){
            numberOfComments = 1
            comments = listOf(commentsString)
        }
        return numberOfComments
    }

    private fun handleLikes(likesString: String): Int{
        val likes = likesString.split(",")
        var numberOfLikes = if (likesString.isNotEmpty()){ 1 }else{ 0 }
        for(like:String in likes){
            numberOfLikes++
        }
        return numberOfLikes
    }
}