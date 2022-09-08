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
import kotlin.random.Random

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

    private val apiInterface by lazy { APIClient().getClient()?.create(APIInterface::class.java) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        etLogInUsername = findViewById(R.id.etLogInUsername)
        etLogInPassword = findViewById(R.id.etLogInPassword)
        btLogInLogin = findViewById(R.id.btLogInLogin)
        btLogInLogin.setOnClickListener {
            getApiKey(etLogInUsername.text.toString(), etLogInPassword.text.toString())
        }

        etRegEmail = findViewById(R.id.etRegEmail)
        etRegUsername = findViewById(R.id.etRegUsername)
        etRegPassword = findViewById(R.id.etRegPassword)
        etRegAbout = findViewById(R.id.etRegAbout)
        btRegRegister = findViewById(R.id.btRegRegister)
        btRegRegister.setOnClickListener {
            if(etRegEmail.text.isNotEmpty()&&etRegUsername.text.isNotEmpty()&&etRegPassword.text.isNotEmpty()){
                newUser(etRegUsername.text.toString(), etRegEmail.text.toString())
            }else{
                // clear shared preferences
//                val sharedPreferences = this.getSharedPreferences(
//                    getString(R.string.preference_file_key), Context.MODE_PRIVATE)
//                sharedPreferences.edit().clear().apply()
                Toast.makeText(this@LoginRegisterActivity, "Email, Username, and Password are required", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getApiKey(username: String, password: String){
        var issue = false
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = apiInterface!!.logIn(username, password)
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
                    logIn(username)
                }else{
                    Toast.makeText(this@LoginRegisterActivity, "Unable to log in. Please check Username and Password", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun logIn(username: String){
        Toast.makeText(this@LoginRegisterActivity, "Logged in successfully", Toast.LENGTH_LONG).show()
        val sharedPreferences = this@LoginRegisterActivity.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("apiKey", userApiKey)
            putString("username", username)
            apply()
        }
        val intent = Intent(this@LoginRegisterActivity, MainActivity::class.java)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun newUser(newUsername: String, newEmail: String){
        CoroutineScope(Dispatchers.IO).launch {
            var issue = false
            val usernames = ArrayList<String>()
            val emails = ArrayList<String>()
            try{
                val response = apiInterface!!.getAllUsers()
                if(response.isSuccessful){
                    for(user:User in response.body()!!){
                        usernames.add(user.username)
                        emails.add(user.email)
                    }
                    if(usernames.contains(newUsername) || emails.contains(newEmail)){
                        Log.d("MAIN", "Username or Email already exists")
                        issue = true
                    }
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
                    apiInterface?.addUser(
                        User(
                            etRegEmail.text.toString(),
                            etRegUsername.text.toString(),
                            etRegPassword.text.toString(),
                            "",
                            "",
                            "",
                            "",
                            if (etRegAbout.text.isNotEmpty()) etRegAbout.text.toString() else "",
                            LocalDateTime.now().toString()
                        )
                    )!!.enqueue(object: Callback<User> {
                        override fun onResponse(call: Call<User>, response: Response<User>) {
                            if(response.code()==201){
                                Log.d("MAIN", "User Created")
                                getApiKey(etRegUsername.text.toString(), etRegPassword.text.toString())
//                                logIn(etRegUsername.text.toString())
                            }
                        }

                        override fun onFailure(call: Call<User>, t: Throwable) {
                            Toast.makeText(this@LoginRegisterActivity, "Something went wrong", Toast.LENGTH_LONG).show()
                        }
                    })
                }else{
                    Toast.makeText(this@LoginRegisterActivity, "User or Email already exists", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}