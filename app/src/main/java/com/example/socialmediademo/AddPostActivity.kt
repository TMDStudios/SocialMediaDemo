package com.example.socialmediademo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.socialmediademo.api.APIClient
import com.example.socialmediademo.api.APIInterface
import com.example.socialmediademo.models.Post
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddPostActivity : AppCompatActivity() {
    private lateinit var etTitle: EditText
    private lateinit var etText: EditText
    private lateinit var btAddPost: Button
    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        etTitle = findViewById(R.id.etAddPostTitle)
        etText = findViewById(R.id.etAddPostText)
        btAddPost = findViewById(R.id.btAddPost)
        btAddPost.setOnClickListener {
            username = intent.getStringExtra("username")

            if(etTitle.text.isNotEmpty() && etText.text.isNotEmpty() && username!=null){
                val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)

                apiInterface?.addPost(
                    Post(
                        0,
                        username!!,
                        etTitle.text.toString(),
                        "",
                        etText.text.toString(),
                        "",
                    )
                )!!.enqueue(object: Callback<Post> {
                    override fun onResponse(call: Call<Post>, response: Response<Post>) {
                        Log.d("MAIN", "Response: $response")
                        val intent = Intent(this@AddPostActivity, MainActivity::class.java)
                        startActivity(intent)
                    }

                    override fun onFailure(call: Call<Post>, t: Throwable) {
                        Toast.makeText(this@AddPostActivity, "Something went wrong", Toast.LENGTH_LONG).show()
                    }
                })
            }else{
                Toast.makeText(this@AddPostActivity, "Please enter title and text", Toast.LENGTH_LONG).show()
            }
        }

    }
}