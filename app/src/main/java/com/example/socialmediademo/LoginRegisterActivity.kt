package com.example.socialmediademo

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.socialmediademo.api.APIClient
import com.example.socialmediademo.api.APIInterface
import com.example.socialmediademo.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime

class LoginRegisterActivity : AppCompatActivity() {
    private lateinit var etLogInUsername: EditText
    private lateinit var etLogInPassword: EditText
    private lateinit var btLogInLogin: Button

    private lateinit var etRegEmail: EditText
    private lateinit var etRegUsername: EditText
    private lateinit var etRegPassword: EditText
    private lateinit var etRegAbout: EditText
    private lateinit var btRegRegister: Button

    private var userApiKey = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)

        etLogInUsername = findViewById(R.id.etLogInUsername)
        etLogInPassword = findViewById(R.id.etLogInPassword)
        btLogInLogin = findViewById(R.id.btLogInLogin)
        btLogInLogin.setOnClickListener {
            var issue = false
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    val response = apiInterface!!.logIn(etLogInUsername.text.toString(), etLogInPassword.text.toString())
                    if(response.isSuccessful){
                        userApiKey = response.body()!!
                    }else{
                        issue = true
                        Log.d("MAIN", "Unable to get data.")
                    }
                }catch(e: Exception){
                    issue = true
                    Log.d("MAIN", "Exception: $e")
                }
                withContext(Dispatchers.Main){
                    if(!issue){
                        Toast.makeText(this@LoginRegisterActivity, "Logged in successfully", Toast.LENGTH_LONG).show()
                        val sharedPreferences = this@LoginRegisterActivity.getSharedPreferences(
                            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString("apiKey", userApiKey)
                            apply()
                        }
                        val intent = Intent(this@LoginRegisterActivity, MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this@LoginRegisterActivity, "Unable to log in. Please check Username and Password", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        etRegEmail = findViewById(R.id.etRegEmail)
        etRegUsername = findViewById(R.id.etRegUsername)
        etRegPassword = findViewById(R.id.etRegPassword)
        etRegAbout = findViewById(R.id.etRegAbout)
        btRegRegister = findViewById(R.id.btRegRegister)
        btRegRegister.setOnClickListener {
            if(etRegEmail.text.isNotEmpty()&&etRegUsername.text.isNotEmpty()&&etRegPassword.text.isNotEmpty()){
                apiInterface?.addUser(
                    User(
                        etRegEmail.text.toString(),
                        etRegUsername.text.toString(),
                        etRegPassword.text.toString(),
                        "a",
                        "a",
                        "a",
                        "a",
                        if (etRegAbout.text.isNotEmpty()) etRegAbout.text.toString() else "a",
                        LocalDateTime.now().toString()
                    )
                )!!.enqueue(object: Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        Log.d("MAIN", "Response: $response")
                        val intent = Intent(this@LoginRegisterActivity, MainActivity::class.java)
                        startActivity(intent)
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Toast.makeText(this@LoginRegisterActivity, "Something went wrong", Toast.LENGTH_LONG).show()
                    }
                })
            }else{
                // clear shared preferences
//                val sharedPreferences = this.getSharedPreferences(
//                    getString(R.string.preference_file_key), Context.MODE_PRIVATE)
//                sharedPreferences.edit().clear().apply()
                Toast.makeText(this@LoginRegisterActivity, "Email, Username, and Password are required", Toast.LENGTH_LONG).show()
            }
        }
    }
}