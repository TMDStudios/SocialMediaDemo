package com.example.socialmediademo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediademo.adapters.RVAdapter
import com.example.socialmediademo.api.APIClient
import com.example.socialmediademo.api.APIInterface
import com.example.socialmediademo.models.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var rvMain: RecyclerView
    private lateinit var rvAdapter: RVAdapter

    private lateinit var btAddPost: Button
    private lateinit var btLogIn: Button
    private lateinit var posts: List<Post>

    private val apiInterface by lazy { APIClient().getClient()?.create(APIInterface::class.java) }

    private var apiKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        posts = listOf()
        rvMain = findViewById(R.id.rvMain)
        rvAdapter = RVAdapter(this, posts)
        rvMain.adapter = rvAdapter
        rvMain.layoutManager = LinearLayoutManager(this)

        btAddPost = findViewById(R.id.btAddPost)
        btAddPost.setOnClickListener {
            apiKey = intent.getStringExtra("apiKey").toString()
            Log.d("MAIN", "Key: $apiKey")
            if(apiKey!=null){
                val intent = Intent(this, AddPostActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this, "You must be logged in to add posts", Toast.LENGTH_LONG).show()
            }
        }
        btLogIn = findViewById(R.id.btLogIn)
        btLogIn.setOnClickListener {
            val intent = Intent(this, LoginRegisterActivity::class.java)
            startActivity(intent)
        }

        getPosts()
    }

    private fun getPosts(){
        CoroutineScope(IO).launch {
            try{
                val response = apiInterface!!.getAllPosts()
                if(response.isSuccessful){
                    posts = response.body()!!
                }else{
                    Log.d("MAIN", "Unable to get data.")
                }
            }catch(e: Exception){
                Log.d("MAIN", "Exception: $e")
            }
            withContext(Main){
                rvAdapter.update(posts)
            }
        }
    }

    fun viewPost(postId: Int){
        val intent = Intent(this, ViewPostActivity::class.java)
        intent.putExtra("postId", postId)
        startActivity(intent)
    }
}