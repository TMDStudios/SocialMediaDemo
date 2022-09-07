package com.example.socialmediademo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.socialmediademo.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private var username: String? = null
    private var user: User? = null

    private lateinit var sharedPreferences: SharedPreferences

    override fun onResume() {
        super.onResume()
        getPosts()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        apiKey = sharedPreferences.getString("apiKey", "")

        posts = listOf()
        rvMain = findViewById(R.id.rvMain)
        rvAdapter = RVAdapter(this, posts)
        rvMain.adapter = rvAdapter
        rvMain.layoutManager = LinearLayoutManager(this)

        btAddPost = findViewById(R.id.btAddPost)
        btAddPost.setOnClickListener {
            Log.d("MAIN", "Key: $apiKey")
            if(apiKey!!.length==64){
                getUserData(apiKey!!)
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

    private fun getUserData(userApiKey: String){
        var issue = false
        CoroutineScope(IO).launch {
            try{
                val response = apiInterface!!.getUser(userApiKey)
                if(response.isSuccessful){
                    user = response.body()!!
                    if(user!=null){
                        username = user!!.username
                        with(sharedPreferences.edit()) {
                            putString("username", username!!)
                            apply()
                        }
                    }
                }else{
                    issue = true
                    Log.d("MAIN", "Unable to get data.")
                }
            }catch(e: Exception){
                issue = true
                Log.d("MAIN", "Exception: $e")
            }
            withContext(Main){
                if(issue){
                    Toast.makeText(this@MainActivity, "Unable to retrieve user information", Toast.LENGTH_LONG).show()
                }else{
                    val intent = Intent(this@MainActivity, AddPostActivity::class.java)
                    intent.putExtra("username", username)
                    startActivity(intent)
                }
            }
        }
    }

}